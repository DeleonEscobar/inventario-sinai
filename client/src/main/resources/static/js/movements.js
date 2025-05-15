import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/movements';
let editing = false;
let currentMovement = null;
let step = 0;
let clients = [];
let batches = [];
let users = [];
let selectedBatches = [];

function showStep(n) {
    $('#stepper .step').addClass('hidden').eq(n).removeClass('hidden');
    step = n;
}

function openModal(edit = false, movement = null) {
    $('#movement-modal').removeClass('hidden');
    editing = edit;
    currentMovement = movement;
    $('#modal-title').text(edit ? 'Editar Movimiento' : 'Nuevo Movimiento');
    $('#movement-form')[0].reset();
    selectedBatches = [];
    showStep(0);
    fillClientsSelect(movement ? movement.client?.id : null);
    fillBatchesList(movement ? movement.batches : []);
    fillUsersSelect(movement ? movement.responsibleUser?.id : null);
    if (edit && movement) {
        // Prellenar pasos si es edición
        $('#movement-client').val(movement.client?.id);
        $('#movement-responsible').val(movement.responsibleUser?.id);
        selectedBatches = movement.batches?.map(b => b.id) || [];
        updateBatchesSelection();
    }
    updateSummary();
}

function closeModal() {
    $('#movement-modal').addClass('hidden');
    $('#movement-form')[0].reset();
    currentMovement = null;
    selectedBatches = [];
}

async function fetchMovements() {
    try {
        $('#movements-table-body').html('<tr><td colspan="6" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
        const userToken = await getTokenRequest();
        if (!userToken) {
            $('#movements-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error de autenticación</td></tr>');
            return;
        }
        const movements = await $.ajax({
            url: apiEndpoint,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        renderMovements(movements);
    } catch (err) {
        $('#movements-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error al cargar movimientos</td></tr>');
    }
}

function renderMovements(movements) {
    const $tbody = $('#movements-table-body');
    if (!movements || !movements.length) {
        $tbody.html('<tr><td colspan="6" class="text-center py-6 text-slate-400">No hay movimientos registrados</td></tr>');
        return;
    }
    $tbody.empty();
    $.each(movements, (i, m) => {
        $tbody.append(`
            <tr>
                <td class="px-6 py-4">${m.name || m.notes}</td>
                <td class="px-6 py-4">${m.client?.name || ''}</td>
                <td class="px-6 py-4">${m.responsibleUser?.name || ''}</td>
                <td class="px-6 py-4">${renderStatus(m.status)}</td>
                <td class="px-6 py-4">${formatDate(m.createdAt)}</td>
                <td class="px-6 py-4 text-center">
                    <!-- CRUD deshabilitado -->
                    <span class="text-slate-400">Solo lectura</span>
                </td>
            </tr>
        `);
    });
    // No asignar eventos de editar/eliminar/ver
}

function renderStatus(status) {
    switch (status) {
        case 1: return '<span class="text-yellow-600 font-bold">Solicitado</span>';
        case 2: return '<span class="text-green-600 font-bold">Completado</span>';
        case 3: return '<span class="text-red-600 font-bold">Cancelado</span>';
        default: return '';
    }
}

async function fillClientsSelect(selectedId = null) {
    const userToken = await getTokenRequest();
    if (!userToken) return;
    clients = await $.ajax({ url: 'http://localhost:8081/api/clients', headers: { 'Authorization': `Bearer ${userToken}` } });
    const $select = $('#movement-client').empty().append('<option value="">Seleccione un cliente</option>');
    $.each(clients, (i, c) => {
        $select.append($('<option>', { value: c.id, text: c.name, selected: selectedId && c.id == selectedId }));
    });
}

async function fillBatchesList(selected = []) {
    const userToken = await getTokenRequest();
    if (!userToken) return;
    batches = await $.ajax({ url: 'http://localhost:8081/api/batches', headers: { 'Authorization': `Bearer ${userToken}` } });
    const $list = $('#batches-list').empty();
    $.each(batches, (i, b) => {
        const checked = selected.find(sel => sel.id === b.id) || selectedBatches.includes(b.id);
        $list.append(`
            <label class="flex items-center gap-2 mb-1">
                <input type="checkbox" class="batch-checkbox" value="${b.id}" ${checked ? 'checked' : ''}>
                <span>${b.serialNumber} - ${b.product?.name || ''} (${b.amount})</span>
            </label>
        `);
    });
    $('.batch-checkbox').on('change', function() {
        const id = parseInt($(this).val(), 10);
        if ($(this).is(':checked')) {
            if (!selectedBatches.includes(id)) selectedBatches.push(id);
        } else {
            selectedBatches = selectedBatches.filter(bid => bid !== id);
        }
        updateSummary();
    });
}

async function fillUsersSelect(selectedId = null) {
    const userToken = await getTokenRequest();
    if (!userToken) return;
    // Solo usuarios con role 2 (responsable de bodega)
    users = await $.ajax({ url: 'http://localhost:8081/api/users?role=2', headers: { 'Authorization': `Bearer ${userToken}` } });
    const $select = $('#movement-responsible').empty().append('<option value="">Seleccione responsable</option>');
    $.each(users, (i, u) => {
        $select.append($('<option>', { value: u.id, text: u.name, selected: selectedId && u.id == selectedId }));
    });
}

function updateBatchesSelection() {
    $('.batch-checkbox').each(function() {
        const id = parseInt($(this).val(), 10);
        $(this).prop('checked', selectedBatches.includes(id));
    });
    updateSummary();
}

function updateSummary() {
    const client = clients.find(c => c.id == $('#movement-client').val());
    const responsible = users.find(u => u.id == $('#movement-responsible').val());
    const selectedBatchObjs = batches.filter(b => selectedBatches.includes(b.id));
    let html = '';
    html += `<div><b>Cliente:</b> ${client ? client.name : ''}</div>`;
    html += `<div><b>Responsable:</b> ${responsible ? responsible.name : ''}</div>`;
    html += `<div><b>Lotes:</b> <ul class="list-disc ml-6">${selectedBatchObjs.map(b => `<li>${b.serialNumber} - ${b.product?.name || ''} (${b.amount})</li>`).join('')}</ul></div>`;
    $('#movement-summary').html(html);
}

$('#btn-add-movement').on('click', () => openModal(false));
$('#close-modal').on('click', closeModal);
$(window).on('click', (e) => {
    if (e.target === $('#movement-modal')[0]) closeModal();
});

$('#movement-form').on('submit', async function(e) {
    e.preventDefault();
    try {
        const userToken = await getTokenRequest();
        if (!userToken) return alert('Error de autenticación');
        const clientId = $('#movement-client').val();
        const responsibleId = $('#movement-responsible').val();
        if (!clientId || !responsibleId || !selectedBatches.length) {
            alert('Debe seleccionar cliente, responsable y al menos un lote.');
            return;
        }
        const client = clients.find(c => c.id == clientId);
        const responsibleUser = users.find(u => u.id == responsibleId);
        const createdByUser = await getCurrentUser(userToken); // Implementa esta función según tu auth
        const selectedBatchObjs = batches.filter(b => selectedBatches.includes(b.id));
        const movement = {
            notes: 'Movimiento de inventario', // Cambiado de 'name' a 'notes' para el backend
            type: 1, // Puedes permitir elegir tipo
            status: 1, // Por defecto solicitado
            client,
            responsibleUser,
            createdByUser,
            createdAt: new Date().toISOString(),
            batches: selectedBatchObjs.map(b => b.id) // Solo IDs para el backend
        };
        await $.ajax({
            url: apiEndpoint,
            method: editing && currentMovement?.id ? 'PUT' : 'POST',
            headers: { 'Authorization': `Bearer ${userToken}` },
            contentType: 'application/json',
            data: JSON.stringify(movement)
        });
        closeModal();
        fetchMovements();
    } catch (err) {
        alert('Error al guardar el movimiento');
    }
});

// Wizard navigation
$('#stepper').on('click', '.next-step', function() {
    if (step === 0 && !$('#movement-client').val()) return alert('Seleccione un cliente');
    if (step === 1 && !selectedBatches.length) return alert('Seleccione al menos un lote');
    if (step === 2 && !$('#movement-responsible').val()) return alert('Seleccione un responsable');
    showStep(step + 1);
    updateSummary();
});
$('#stepper').on('click', '.prev-step', function() {
    showStep(step - 1);
    updateSummary();
});

$(document).ready(fetchMovements);
