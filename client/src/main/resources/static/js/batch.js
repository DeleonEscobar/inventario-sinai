import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/batches';
let editing = false;
let productsList = [];

async function fetchProductsForSelect() {
    try {
        const userToken = await getTokenRequest();
        if (!userToken) return [];
        
        const res = await $.ajax({
            url: 'http://localhost:8081/api/products',
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        
        return res;
    } catch (err) {
        console.error('Error al cargar productos para lotes:', err);
        return [];
    }
}

async function fillProductSelect(selectedId = null) {
    productsList = await fetchProductsForSelect();
    const $select = $('#batch-product').empty()
        .append('<option value="">Seleccione un producto</option>');
    
    $.each(productsList, (i, p) => {
        $select.append($('<option>', {
            value: p.id,
            text: p.name,
            selected: selectedId && p.id == selectedId
        }));
    });
}

function openModal(edit = false, batch = null) {
    $('#batch-modal').removeClass('hidden');
    editing = edit;
    
    $('#modal-title').text(edit ? 'Editar Lote' : 'Agregar Lote');
    $('#batch-form')[0].reset();
    $('#batch-id').val('');
    
    if (edit && batch) {
        $('#batch-id').val(batch.id);
        $('#batch-serial').val(batch.serialNumber);
        $('#batch-amount').val(batch.amount);
        $('#batch-price').val(batch.price);
        
        if (batch.expirationDate) {
            $('#batch-expiration').val(batch.expirationDate.split('T')[0]);
        }
        
        fillProductSelect(batch.product ? batch.product.id : null);
    } else {
        fillProductSelect();
    }
}

function closeModal() {
    $('#batch-modal').addClass('hidden');
    $('#batch-form')[0].reset();
}

async function fetchBatches() {
    try {
        $('#batches-table-body').html('<tr><td colspan="9" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
        
        const userToken = await getTokenRequest();
        if (!userToken) {
            $('#batches-table-body').html('<tr><td colspan="9" class="text-center py-6 text-red-500">Error de autenticación</td></tr>');
            return;
        }
        
        const batches = await $.ajax({
            url: apiEndpoint,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        
        renderBatches(batches);
    } catch (err) {
        console.error('Error al cargar lotes:', err);
        $('#batches-table-body').html('<tr><td colspan="9" class="text-center py-6 text-red-500">Error al cargar lotes</td></tr>');
    }
}

function renderBatches(batches) {
    const $tbody = $('#batches-table-body');
    
    if (!batches || !batches.length) {
        $tbody.html('<tr><td colspan="9" class="text-center py-6 text-slate-400">No hay lotes registrados</td></tr>');
        return;
    }
    
    $tbody.empty();
    
    $.each(batches, (i, batch) => {
        $tbody.append(`
            <tr>
                <td class="px-6 py-4">${batch.serialNumber}</td>
                <td class="px-6 py-4">${batch.product ? batch.product.name : ''}</td>
                <td class="px-6 py-4">${batch.amount}</td>
                <td class="px-6 py-4">$${batch.price?.toFixed(2) ?? ''}</td>
                <td class="px-6 py-4">${formatDate(batch.expirationDate)}</td>
                <td class="px-6 py-4">${formatDate(batch.createdAt)}</td>
                <td class="px-6 py-4">${formatDate(batch.updatedAt)}</td>
                <td class="px-6 py-4 text-center">
                    <button class="edit-btn text-green-600 hover:underline mr-2" data-id="${batch.id}">Editar</button>
                    <button class="delete-btn text-red-600 hover:underline" data-id="${batch.id}">Eliminar</button>
                    <button class="goto-product-btn text-blue-600 hover:underline ml-2" 
                        data-product-id="${batch.product ? batch.product.id : ''}">Editar Producto</button>
                </td>
            </tr>
        `);
    });
    
    assignButtonEvents();
}

function assignButtonEvents() {
    $('.edit-btn').off('click').on('click', async function() {
        try {
            const id = $(this).data('id');
            const userToken = await getTokenRequest();
            
            if (!userToken) {
                alert('Error de autenticación');
                return;
            }
            
            const batch = await $.ajax({
                url: `${apiEndpoint}/${id}`,
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            
            openModal(true, batch);
        } catch (err) {
            alert('No se pudo cargar la información del lote');
        }
    });
    
    $('.delete-btn').off('click').on('click', async function() {
        const id = $(this).data('id');
        
        if (!confirm('¿Seguro que deseas eliminar este lote?')) return;
        
        try {
            const userToken = await getTokenRequest();
            
            if (!userToken) {
                alert('Error de autenticación');
                return;
            }
            
            await $.ajax({
                url: `${apiEndpoint}/${id}`,
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            
            fetchBatches();
        } catch (err) {
            alert('No se pudo eliminar el lote');
        }
    });
    
    $('.goto-product-btn').off('click').on('click', function() {
        const productId = $(this).data('product-id');
        
        if (productId && confirm('Vas a ser redirigido a la edición del producto. Si tienes cambios sin guardar en lotes, se perderán. ¿Continuar?')) {
            window.location.href = `/dashboard/admin/products?editProductId=${productId}`;
        }
    });
}

// Event Handlers
$('#btn-add-batch').on('click', () => openModal(false));
$('#close-modal, #cancel-modal').on('click', closeModal);
$(window).on('click', (e) => {
    if (e.target === $('#batch-modal')[0]) closeModal();
});

$('#batch-form').on('submit', async function(e) {
    e.preventDefault();
    
    try {
        const id = $('#batch-id').val();
        const productId = parseInt($('#batch-product').val(), 10);
        
        if (!productId) {
            alert('Debe seleccionar un producto válido.');
            return;
        }
        
        let expirationDate = $('#batch-expiration').val();
        if (expirationDate && expirationDate.length === 10) {
            expirationDate = expirationDate + 'T00:00:00Z';
        }
        
        const batch = {
            serialNumber: $('#batch-serial').val(),
            product: { id: productId },
            amount: parseInt($('#batch-amount').val(), 10),
            price: parseFloat($('#batch-price').val()),
            expirationDate
        };
        
        if (isNaN(batch.amount) || isNaN(batch.price)) {
            alert('Cantidad y precio deben ser valores numéricos válidos.');
            return;
        }
        
        const userToken = await getTokenRequest();
        if (!userToken) {
            alert('Error de autenticación');
            return;
        }
        
        await $.ajax({
            url: editing && id ? `${apiEndpoint}/${id}` : apiEndpoint,
            method: editing && id ? 'PUT' : 'POST',
            headers: { 'Authorization': `Bearer ${userToken}` },
            contentType: 'application/json',
            data: JSON.stringify(batch)
        });
        
        closeModal();
        fetchBatches();
    } catch (err) {
        alert('Error al guardar el lote');
    }
});

$(document).ready(fetchBatches);