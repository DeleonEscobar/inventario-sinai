import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

// Variables globales
const apiEndpoint = 'http://localhost:8081/api/products';
let editing = false;

// Inicializar cuando el documento esté listo
$(document).ready(function() {
    initEventHandlers();
    fetchProducts();
    
    // Si hay un editProductId en la URL, abrir el modal para editarlo
    const urlParams = new URLSearchParams(window.location.search);
    const editProductId = urlParams.get('editProductId');
    if (editProductId) {
        fetchAndEditProduct(editProductId);
    }
});

function initEventHandlers() {
    // Botones del modal
    $('#btn-add-product').on('click', () => openModal(false));
    $('#close-modal, #cancel-modal').on('click', closeModal);
    
    // Cerrar modal al hacer clic fuera
    $(window).on('click', (e) => {
        if (e.target === $('#product-modal')[0]) closeModal();
    });
    
    // Manejar envío del formulario
    $('#product-form').on('submit', saveProduct);
}

function openModal(edit = false, product = null) {
    $('#product-modal').removeClass('hidden');
    editing = edit;
    
    if (edit && product) {
        $('#modal-title').text('Editar Producto');
        $('#product-id').val(product.id);
        $('#product-name').val(product.name);
    } else {
        $('#modal-title').text('Agregar Producto');
        $('#product-form')[0].reset();
        $('#product-id').val('');
    }
}

function closeModal() {
    $('#product-modal').addClass('hidden');
    $('#product-form')[0].reset();
    $('#product-id').val('');
}

async function fetchProducts() {
    try {
        $('#products-table-body').html('<tr><td colspan="4" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
        
        const userToken = await getTokenRequest();
        if (!userToken) {
            $('#products-table-body').html('<tr><td colspan="4" class="text-center py-6 text-red-500">Error de autenticación</td></tr>');
            return;
        }
        
        const products = await $.ajax({
            url: apiEndpoint,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        
        renderProducts(products);
    } catch (err) {
        console.error('Error al cargar productos:', err);
        $('#products-table-body').html('<tr><td colspan="4" class="text-center py-6 text-red-500">Error al cargar productos</td></tr>');
    }
}

function renderProducts(products) {
    const $tbody = $('#products-table-body');
    
    if (!products || !products.length) {
        $tbody.html('<tr><td colspan="4" class="text-center py-6 text-slate-400">No hay productos registrados</td></tr>');
        return;
    }
    
    $tbody.empty();
    
    $.each(products, function(i, product) {
        $('<tr>')
            .append(`<td class="px-6 py-4">${product.name}</td>`)
            .append(`<td class="px-6 py-4">${formatDate(product.createdAt)}</td>`)
            .append(`<td class="px-6 py-4">${formatDate(product.updatedAt)}</td>`)
            .append(`
                <td class="px-6 py-4 text-center">
                    <button class="edit-btn text-blue-600 hover:underline mr-2" data-id="${product.id}">Editar</button>
                    <button class="delete-btn text-red-600 hover:underline" data-id="${product.id}">Eliminar</button>
                </td>
            `)
            .appendTo($tbody);
    });
    
    // Asignar eventos a los botones
    assignButtonEvents();
}

function assignButtonEvents() {
    // Botón editar
    $('.edit-btn').on('click', function() {
        const id = $(this).data('id');
        fetchAndEditProduct(id);
    });
    
    // Botón eliminar
    $('.delete-btn').on('click', async function() {
        const id = $(this).data('id');
        
        if (!confirm('¿Seguro que deseas eliminar este producto?')) return;
        
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
            
            fetchProducts();
        } catch (err) {
            console.error('Error al eliminar el producto:', err);
            alert('No se pudo eliminar el producto');
        }
    });
}

async function fetchAndEditProduct(id) {
    try {
        const userToken = await getTokenRequest();
        if (!userToken) {
            alert('Error de autenticación');
            return;
        }
        
        const product = await $.ajax({
            url: `${apiEndpoint}/${id}`,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        
        openModal(true, product);
    } catch (err) {
        console.error('Error al obtener detalles del producto:', err);
        alert('No se pudo cargar la información del producto');
    }
}

async function saveProduct(e) {
    e.preventDefault();
    
    try {
        const id = $('#product-id').val();
        const product = {
            name: $('#product-name').val(),
        };
        
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
            data: JSON.stringify(product)
        });
        
        closeModal();
        fetchProducts();
    } catch (err) {
        console.error('Error al guardar el producto:', err);
        alert('Error al guardar el producto');
    }
}