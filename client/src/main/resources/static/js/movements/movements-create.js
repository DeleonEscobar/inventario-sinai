import { getTokenRequest } from '/js/utils/getTokenRequest.js';

const apiEndpoint = 'http://localhost:8081/api';

$(document).ready(function() {
    const $clientSelect = $('#clientId');
    const $typeSelect = $('#type');
    const $clientSection = $clientSelect.closest('.mb-6');
    const $batchesLoading = $('#batchesLoading');
    const $batchesContainer = $('#batchesContainer');
    const $availableBatchesList = $('#availableBatchesList');
    const $selectedBatchesList = $('#selectedBatchesList');
    const $batchIdsInput = $('#batchIdsInput');
    const $moveToSelected = $('#moveToSelected');
    const $moveToAvailable = $('#moveToAvailable');
    const $batchSectionToggle = $('#batchSectionToggle');
    const $batchSectionContent = $('#batchSectionContent');
    
    // Datos de lotes disponibles
    let allBatches = [];
    let selectedBatchIds = new Set();
    
    // Toggle para la sección de lotes - hacemos esto más explícito
    $batchSectionToggle.click(function(e) {
        e.preventDefault();
        
        if ($batchSectionContent.hasClass('hidden')) {
            $batchSectionContent.removeClass('hidden');
            $batchSectionToggle.html('Ocultar sección de lotes <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 inline" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M14.707 12.707a1 1 0 01-1.414 0L10 9.414l-3.293 3.293a1 1 0 01-1.414-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 010 1.414z" clip-rule="evenodd" /></svg>');
        } else {
            $batchSectionContent.addClass('hidden');
            $batchSectionToggle.html('Mostrar sección de lotes <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 inline" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" /></svg>');
        }
    });
    
    // Actualizar el disclaimer para gerentes
    $('.disclaimer').html(`
        <span class="font-medium">💡 Nota:</span> La asignación de lotes es una tarea logística que normalmente realiza el encargado correspondiente. 
        Sin embargo, como gerente, puedes visualizar y asignar los lotes si es necesario.
    `);
    
    // Función para actualizar la sección del cliente según el tipo de movimiento
    function updateClientSection() {
        const movementType = $typeSelect.val();
        const clientLabel = $clientSection.find('label[for="clientId"]');
        const clientTitle = $clientSection.find('h3');
        
        if (movementType === 'traslado') {
            clientLabel.text('Cliente (Opcional)');
            $clientSelect.prop('required', false);
            clientTitle.text('2. Información del Cliente (Opcional para Traslados)');
        } else {
            clientLabel.text('Cliente');
            $clientSelect.prop('required', true);
            clientTitle.text('2. Información del Cliente');
        }
    }
    
    // Actualizar la sección del cliente al cambiar el tipo de movimiento
    $typeSelect.on('change', updateClientSection);
    
    // Actualizar la sección del cliente al cargar la página
    updateClientSection();
    
    // Cargar todos los lotes disponibles al iniciar
    loadAvailableBatches();
    
    async function loadAvailableBatches() {
        $batchesLoading.text('Cargando lotes disponibles...');
        $batchesLoading.removeClass('hidden');
        $batchesContainer.addClass('hidden');
        
        try {
            const userToken = await getTokenRequest();
            if (!userToken) {
                $batchesLoading.text('Error de autenticación');
                return;
            }
            
            // Obtener los lotes disponibles (no relacionados con otros movimientos)
            const batches = await $.ajax({
                url: `${apiEndpoint}/batches`,
                method: 'GET',
                dataType: 'json',
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            
            allBatches = batches;
            selectedBatchIds.clear();
            
            if (batches.length === 0) {
                $batchesLoading.text('No hay lotes disponibles en el sistema');
                $batchesLoading.removeClass('hidden');
                $batchesContainer.addClass('hidden');
                return;
            }
            
            updateBatchLists();
            $batchesLoading.addClass('hidden');
            $batchesContainer.removeClass('hidden');
        } catch (error) {
            console.error('Error al cargar los lotes:', error);
            $batchesLoading.text('Error al cargar los lotes. Intente nuevamente.');
            $batchesLoading.removeClass('hidden');
            $batchesContainer.addClass('hidden');
        }
    }
    
    // Actualizar las listas de lotes
    function updateBatchLists() {
        // Limpiar listas
        $availableBatchesList.empty();
        $selectedBatchesList.empty();
        
        // Actualizar campo oculto
        $batchIdsInput.val(Array.from(selectedBatchIds).join(','));
        
        // Llenar lista de lotes disponibles
        $.each(allBatches, function(i, batch) {
            if (!selectedBatchIds.has(batch.id.toString())) {
                const $item = createBatchItem(batch);
                $item.on('click', function() {
                    $(this).toggleClass('selected');
                });
                $availableBatchesList.append($item);
            }
        });
        
        // Llenar lista de lotes seleccionados
        $.each(allBatches, function(i, batch) {
            if (selectedBatchIds.has(batch.id.toString())) {
                const $item = createBatchItem(batch);
                $item.on('click', function() {
                    $(this).toggleClass('selected');
                });
                $selectedBatchesList.append($item);
            }
        });
    }
    
    // Crear elemento de lote
    function createBatchItem(batch) {
        return $(`
            <div class="batch-item grid grid-cols-3" data-id="${batch.id}">
                <div>${batch.serialNumber}</div>
                <div>${batch.product.name}</div>
                <div>${batch.amount}</div>
            </div>
        `);
    }
    
    // Seleccionar un lote
    function selectBatch(batchId) {
        selectedBatchIds.add(batchId);
        updateBatchLists();
    }
    
    // Eliminar un lote de la selección
    function unselectBatch(batchId) {
        selectedBatchIds.delete(batchId);
        updateBatchLists();
    }
    
    // Manejar botones de transferencia
    $moveToSelected.on('click', function() {
        const selectedItems = $('#availableBatchesList .batch-item.selected');
        if (selectedItems.length === 0) {
            if ($('#availableBatchesList .batch-item').length > 0) {
                alert('Seleccione uno o más lotes de la lista izquierda para moverlos');
            }
            return;
        }
        
        selectedItems.each(function() {
            selectBatch($(this).data('id').toString());
        });
    });
    
    $moveToAvailable.on('click', function() {
        const selectedItems = $('#selectedBatchesList .batch-item.selected');
        if (selectedItems.length === 0) {
            if ($('#selectedBatchesList .batch-item').length > 0) {
                alert('Seleccione uno o más lotes de la lista derecha para moverlos');
            }
            return;
        }
        
        selectedItems.each(function() {
            unselectBatch($(this).data('id').toString());
        });
    });
    
    // Doble clic para mover directamente
    $(document).on('dblclick', '#availableBatchesList .batch-item', function() {
        selectBatch($(this).data('id').toString());
    });
    
    $(document).on('dblclick', '#selectedBatchesList .batch-item', function() {
        unselectBatch($(this).data('id').toString());
    });
    
    // Validar el formulario antes de enviar
    window.validateForm = function() {
        const clientId = $('#clientId').val();
        const responsibleId = $('#responsibleId').val();
        const notes = $('#notes').val();
        const type = $('#type').val();
        
        if (type == 'pedido' && !clientId) {
            alert('Por favor seleccione un cliente.');
            return false;
        }
        
        if (!responsibleId) {
            alert('Por favor seleccione un responsable.');
            return false;
        }
        
        if (!notes) {
            alert('Por favor ingrese una nota para el movimiento.');
            return false;
        } else if (notes.length > 255) {
            alert('La nota no puede exceder los 255 caracteres.');
            return false;
        }

        if (selectedBatchIds.size === 0) {
            alert('Por favor seleccione al menos un lote.');
            return false;
        }
        
        return true;
    };
}); 