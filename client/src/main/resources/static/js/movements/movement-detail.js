import { getTokenRequest } from '/js/utils/getTokenRequest.js';

// Configuración de la API
const apiEndpoint = 'http://localhost:8081/api';

$(document).ready(function() {
    const $batchSectionContent = $('#batchSectionContent');
    const $batchesLoading = $('#batchesLoading');
    const $batchesContainer = $('#batchesContainer');
    const $availableBatchesList = $('#availableBatchesList');
    const $selectedBatchesList = $('#selectedBatchesList');
    const $batchIdsInput = $('#batchIdsInput');
    const $moveToSelected = $('#moveToSelected');
    const $moveToAvailable = $('#moveToAvailable');
    const $updateBatchesBtn = $('#updateBatchesBtn');
    const $batchInstructions = $('#batchInstructions');
    const movementId = $('#movementId').val();
    
    // Datos de lotes
    let allBatches = [];
    let selectedBatchIds = new Set();
    let isReadOnly = false;
    
    // Verificar si el movimiento está completado o cancelado
    const movementStatus = parseInt($('#movementStatus').val());
    if (movementStatus === 3 || movementStatus === 4) {
        isReadOnly = true;
        // Ocultar botones de gestión
        $moveToSelected.hide();
        $moveToAvailable.hide();
        $updateBatchesBtn.hide();
        
        // Mostrar mensaje de solo lectura
        const statusName = movementStatus === 3 ? 'completado' : 'cancelado';
        $('#batchManagementContainer').prepend(`
            <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
                <div class="flex items-start gap-3">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                    <div>
                        <p class="font-medium text-yellow-800">Este movimiento está ${statusName}</p>
                        <p class="text-sm text-yellow-700 mt-1">Los lotes asociados no se pueden modificar. Solo puede ver los lotes actuales.</p>
                    </div>
                </div>
            </div>
        `);
    }
    
    // Mostrar la sección de lotes si estaba oculta
    if ($batchSectionContent.hasClass('hidden')) {
        $batchSectionContent.removeClass('hidden');
    }
    
    // Cargar todos los lotes al iniciar
    loadBatches();
    
    async function loadBatches() {
        $batchesLoading.removeClass('hidden');
        $batchInstructions.removeClass('hidden');
        $batchesContainer.addClass('hidden');
        
        try {
            const userToken = await getTokenRequest();
            if (!userToken) {
                $batchesLoading.text('Error de autenticación');
                return;
            }
            
            // Obtener movimiento actual con sus lotes
            const movement = await $.ajax({
                url: `${apiEndpoint}/movements/${movementId}`,
                method: 'GET',
                dataType: 'json',
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            
            // Si es de solo lectura, solo mostramos los lotes del movimiento
            if (isReadOnly) {
                allBatches = movement.batches || [];
                selectedBatchIds = new Set(allBatches.map(batch => batch.id.toString()));
                
                // Ocultar la columna de lotes disponibles y ajustar el ancho
                $('#availableBatchesSection').hide();
                $batchInstructions.addClass('hidden');
                $('#selectedBatchesSection').removeClass('md:grid-cols-2').addClass('md:grid-cols-1');
                
                updateBatchLists();
                $batchesLoading.addClass('hidden');
                $batchesContainer.removeClass('hidden');
                return;
            }
            
            // Para modo edición, cargar también los lotes disponibles
            const availableBatches = await $.ajax({
                url: '/dashboard/employee/movements/api/batches',
                method: 'GET',
                dataType: 'json'
            });
            
            // Combinar todos los lotes
            allBatches = [...availableBatches];
            const movementBatches = movement.batches || [];
            
            // Añadir los lotes del movimiento a la lista de todos los lotes si no están ya
            movementBatches.forEach(movementBatch => {
                const exists = allBatches.some(batch => batch.id === movementBatch.id);
                if (!exists) {
                    allBatches.push(movementBatch);
                }
            });
            
            // Inicializar los IDs de lotes seleccionados con los lotes actuales del movimiento
            selectedBatchIds = new Set(movementBatches.map(batch => batch.id.toString()));
            
            updateBatchLists();
            $batchesLoading.addClass('hidden');
            $batchesContainer.removeClass('hidden');
        } catch (error) {
            console.error('Error al cargar los lotes:', error);
            $batchesLoading.text('Error al cargar los lotes. Intente nuevamente.');
        }
    }
    
    // Actualizar las listas de lotes
    function updateBatchLists() {
        // Limpiar listas
        $availableBatchesList.empty();
        $selectedBatchesList.empty();
        
        // Actualizar campo oculto
        $batchIdsInput.val(Array.from(selectedBatchIds).join(','));
        
        if (!isReadOnly) {
            // Llenar lista de lotes disponibles (solo en modo edición)
            allBatches.forEach(batch => {
                if (!selectedBatchIds.has(batch.id.toString())) {
                    const $item = createBatchItem(batch);
                    $item.on('click', function() {
                        $(this).toggleClass('selected');
                    });
                    $item.on('dblclick', function() {
                        selectBatch(batch.id.toString());
                    });
                    $availableBatchesList.append($item);
                }
            });
        }
        
        // Llenar lista de lotes seleccionados
        allBatches.forEach(batch => {
            if (selectedBatchIds.has(batch.id.toString())) {
                const $item = createBatchItem(batch);
                
                if (!isReadOnly) {
                    // Solo agregar eventos de selección si no es de solo lectura
                    $item.on('click', function() {
                        $(this).toggleClass('selected');
                    });
                    $item.on('dblclick', function() {
                        unselectBatch(batch.id.toString());
                    });
                } else {
                    // Estilo especial para modo solo lectura
                    $item.addClass('read-only');
                }
                
                $selectedBatchesList.append($item);
            }
        });
        
        // Cambiar título si está en modo solo lectura
        if (isReadOnly) {
            $('#selectedBatchesTitle').text('(Solo Lectura)');
        }
    }
    
    // Crear elemento de lote
    function createBatchItem(batch) {
        return $(`
            <div class="batch-item p-3 hover:bg-slate-50 ${isReadOnly ? 'cursor-default' : 'cursor-pointer'}" data-id="${batch.id}">
                <div class="grid grid-cols-3">
                    <div>${batch.serialNumber}</div>
                    <div>${batch.product.name}</div>
                    <div>${batch.amount}</div>
                </div>
            </div>
        `);
    }
    
    // Seleccionar un lote
    function selectBatch(batchId) {
        if (isReadOnly) return;
        selectedBatchIds.add(batchId);
        updateBatchLists();
    }
    
    // Eliminar un lote de la selección
    function unselectBatch(batchId) {
        if (isReadOnly) return;
        selectedBatchIds.delete(batchId);
        updateBatchLists();
    }
    
    // Manejar botones de transferencia
    $moveToSelected.on('click', function() {
        if (isReadOnly) return;
        
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
        if (isReadOnly) return;
        
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
    
    // Guardar cambios en los lotes
    $updateBatchesBtn.on('click', async function() {
        if (isReadOnly) return;
        
        try {
            const userToken = await getTokenRequest();
            if (!userToken) {
                alert('Error de autenticación');
                return;
            }
            
            // Enviar actualización de lotes
            await $.ajax({
                url: `/dashboard/employee/movements/${movementId}/update-batches`,
                method: 'POST',
                data: { batchIds: Array.from(selectedBatchIds).join(',') }
            });
            
            // Recargar la página para mostrar los cambios
            alert('Lotes actualizados correctamente');
            window.location.reload();
        } catch (error) {
            console.error('Error al actualizar los lotes:', error);
            alert('Error al actualizar los lotes. Por favor, intente nuevamente.');
        }
    });
}); 