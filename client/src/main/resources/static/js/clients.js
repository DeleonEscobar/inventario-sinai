import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/clients';
let editing = false;

function openModal(edit = false, client = null) {
    $('#client-modal').removeClass('hidden');
    editing = edit;
    $('#modal-title').text(edit ? 'Editar Cliente' : 'Agregar Cliente');
    $('#client-form')[0].reset();
    $('#client-id').val('');
    if (edit && client) {
        $('#client-id').val(client.id);
        $('#client-name').val(client.name);
        $('#client-address').val(client.address);
    }
}

function closeModal() {
    $('#client-modal').addClass('hidden');
    $('#client-form')[0].reset();
    $('#client-id').val('');
}

async function fetchClients() {
    try {
        $('#clients-table-body').html('<tr><td colspan="6" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
        const userToken = await getTokenRequest();
        if (!userToken) {
            $('#clients-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error de autenticación</td></tr>');
            return;
        }
        const clients = await $.ajax({
            url: apiEndpoint,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        renderClients(clients);
    } catch (err) {
        $('#clients-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error al cargar clientes</td></tr>');
    }
}

function renderClients(clients) {
    const $tbody = $('#clients-table-body');
    if (!clients || !clients.length) {
        $tbody.html('<tr><td colspan="5" class="text-center py-6 text-slate-400">No hay clientes registrados</td></tr>');
        return;
    }
    $tbody.empty();
    $.each(clients, (i, client) => {
        $tbody.append(`
            <tr>
                <td class="px-6 py-4">${client.name}</td>
                <td class="px-6 py-4">${client.address}</td>
                <td class="px-6 py-4">${formatDate(client.createdAt)}</td>
                <td class="px-6 py-4">${formatDate(client.updatedAt)}</td>
                <td class="px-6 py-4 text-center">
                    <button class="edit-btn text-blue-600 hover:underline mr-2" data-id="${client.id}">Editar</button>
                    <button class="delete-btn text-red-600 hover:underline" data-id="${client.id}">Eliminar</button>
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
            const client = await $.ajax({
                url: `${apiEndpoint}/${id}`,
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            openModal(true, client);
        } catch (err) {
            alert('No se pudo cargar la información del cliente');
        }
    });
    $('.delete-btn').off('click').on('click', async function() {
        const id = $(this).data('id');
        if (!confirm('¿Seguro que deseas eliminar este cliente?')) return;
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
            fetchClients();
        } catch (err) {
            alert('No se pudo eliminar el cliente');
        }
    });
}

$('#btn-add-client').on('click', () => openModal(false));
$('#close-modal, #cancel-modal').on('click', closeModal);
$(window).on('click', (e) => {
    if (e.target === $('#client-modal')[0]) closeModal();
});

$('#client-form').on('submit', async function(e) {
    e.preventDefault();
    try {
        const id = $('#client-id').val();
        const client = {
            name: $('#client-name').val(),
            address: $('#client-address').val()
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
            data: JSON.stringify(client)
        });
        closeModal();
        fetchClients();
    } catch (err) {
        alert('Error al guardar el cliente');
    }
});

$(document).ready(fetchClients);
