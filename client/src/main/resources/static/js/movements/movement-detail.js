import { getTokenRequest } from '/js/utils/getTokenRequest.js';

const apiEndpoint = 'http://localhost:8081/api';

$(document).ready(function() {
    const movementId = $('#movementId').val();
    const $batchesLoading = $('#batchesLoading');
    const $batchesContainer = $('#batchesContainer');
    const $availableBatchesList = $('#availableBatchesList');
    const $selectedBatchesList = $('#selectedBatchesList');
    const $batchIdsInput = $('#batchIdsInput');
    const $moveToSelected = $('#moveToSelected');
    const $moveToAvailable = $('#moveToAvailable');
    const $batchSectionToggle = $('#batchSectionToggle');
    const $batchSectionContent = $('#batchSectionContent');
    const $updateBatchesBtn = $('#updateBatchesBtn');
    
    // Datos de lotes disponibles
    let allBatches = [];
    let movementBatches = [];
    let selectedBatchIds = new Set();
    
    // Toggle para la sección de lotes
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
    
    // Cargar los lotes al iniciar
    loadBatches();
    
    async function loadBatches() {
        $batchesLoading.text('Cargando lotes disponibles...');
        $batchesLoading.removeClass('hidden');
        $batchesContainer.addClass('hidden');
        
        try {
            const userToken = await getTokenRequest();
            if (!userToken) {
                $batchesLoading.text('Error de autenticación');
                return;
            }
            
            // Obtener los lotes disponibles y los lotes del movimiento actual
            const [available, current] = await Promise.all([
                $.ajax({
                    url: `${apiEndpoint}/batches/available`,
                    method: 'GET',
                    dataType: 'json',
                    headers: { 'Authorization': `Bearer ${userToken}` }
                }),
                $.ajax({
                    url: `${apiEndpoint}/movements/${movementId}/batches`,
                    method: 'GET',
                    dataType: 'json',
                    headers: { 'Authorization': `Bearer ${userToken}` }
                })
            ]);
            
            // Combinar los lotes disponibles con los lotes del movimiento
            allBatches = [...available];
            movementBatches = current;
            
            // Inicializar los lotes seleccionados con los lotes actuales del movimiento
            selectedBatchIds = new Set(movementBatches.map(batch => batch.id.toString()));
            
            if (allBatches.length === 0 && movementBatches.length === 0) {
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
        
        // Combinar todos los lotes (disponibles + los del movimiento actual)
        const combinedBatches = [...allBatches, ...movementBatches.filter(mb => 
            !allBatches.some(ab => ab.id === mb.id))];
        
        // Llenar lista de lotes disponibles
        combinedBatches.forEach(batch => {
            if (!selectedBatchIds.has(batch.id.toString())) {
                const $item = createBatchItem(batch);
                $item.on('click', function() {
                    $(this).toggleClass('selected');
                });
                $availableBatchesList.append($item);
            }
        });
        
        // Llenar lista de lotes seleccionados
        combinedBatches.forEach(batch => {
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
    
    // Actualizar lotes del movimiento
    $updateBatchesBtn.on('click', async function(e) {
        e.preventDefault();
        
        try {
            const userToken = await getTokenRequest();
            if (!userToken) {
                alert('Error de autenticación');
                return;
            }
            
            const batchIds = Array.from(selectedBatchIds);
            
            await $.ajax({
                url: `${apiEndpoint}/movements/${movementId}/batches`,
                method: 'PUT',
                contentType: 'application/json',
                headers: { 'Authorization': `Bearer ${userToken}` },
                data: JSON.stringify({ batchIds: batchIds })
            });
            
            // Recargar la página para mostrar los cambios
            window.location.reload();
        } catch (error) {
            console.error('Error al actualizar los lotes:', error);
            alert('Ocurrió un error al actualizar los lotes del movimiento');
        }
    });
}); 