import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/users';
let editing = false;
let currentUser = null;

// Obtener el usuario actual de la sesión
async function getCurrentUser() {
    try {
        // Obtener el usuario de la sesión desde un atributo data del body o cualquier elemento donde esté almacenado
        const userJson = $('body').data('user');
        if (userJson) {
            currentUser = typeof userJson === 'string' ? JSON.parse(userJson) : userJson;
        }
        
        // Si no está disponible, intentar obtenerlo de otra manera (ajustar según tu implementación)
        if (!currentUser || !currentUser.id) {
            const userToken = await getTokenRequest();
            if (userToken) {
                // Obtener información del usuario actual desde otra fuente si está disponible
                // Por ejemplo, desde un endpoint específico
                // Esta parte puede variar según cómo esté implementada tu aplicación
            }
        }
    } catch (err) {
        console.error('Error al obtener el usuario actual:', err);
    }
}

function openModal(edit = false, user = null) {
    $('#user-modal').removeClass('hidden');
    editing = edit;
    $('#modal-title').text(edit ? 'Editar Usuario' : 'Agregar Usuario');
    $('#user-form')[0].reset();
    $('#user-id').val('');
    if (edit && user) {
        $('#user-id').val(user.id);
        $('#user-name').val(user.name);
        $('#user-username').val(user.username);
        $('#user-dui').val(user.dui);
        $('#user-role').val(user.role);
        // No mostramos la contraseña en la edición para seguridad
        $('#user-password').val('').attr('placeholder', 'Dejar en blanco para mantener la actual');
        $('#user-password').prop('required', false);
    } else {
        $('#user-password').attr('placeholder', '').prop('required', true);
    }
}

function closeModal() {
    $('#user-modal').addClass('hidden');
    $('#user-form')[0].reset();
    $('#user-id').val('');
}

async function fetchUsers() {
    try {
        $('#users-table-body').html('<tr><td colspan="6" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
        const userToken = await getTokenRequest();
        if (!userToken) {
            $('#users-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error de autenticación</td></tr>');
            return;
        }
        const users = await $.ajax({
            url: apiEndpoint,
            headers: { 'Authorization': `Bearer ${userToken}` }
        });
        await getCurrentUser();
        renderUsers(users);
    } catch (err) {
        $('#users-table-body').html('<tr><td colspan="6" class="text-center py-6 text-red-500">Error al cargar usuarios</td></tr>');
    }
}

function renderUsers(users) {
    const $tbody = $('#users-table-body');
    if (!users || !users.length) {
        $tbody.html('<tr><td colspan="6" class="text-center py-6 text-slate-400">No hay usuarios registrados</td></tr>');
        return;
    }
    $tbody.empty();
    $.each(users, (i, user) => {
        let roleName = '';
        switch(user.role) {
            case 1: roleName = 'Administrador'; break;
            case 2: roleName = 'Operador'; break;
            default: roleName = 'Desconocido';
        }
        
        // Verificar si es el usuario actual
        const isCurrentUser = currentUser && currentUser.id === user.id;
        
        // Crear la fila con clase de resaltado si es el usuario actual
        const rowClass = isCurrentUser ? 'bg-slate-100' : '';
        
        // Preparar los botones según si es el usuario actual o no
        const actionButtons = isCurrentUser 
            ? `<button class="edit-btn text-blue-600 hover:underline mr-2" data-id="${user.id}">Editar</button>`
            : `<button class="edit-btn text-blue-600 hover:underline mr-2" data-id="${user.id}">Editar</button>
               <button class="delete-btn text-red-600 hover:underline" data-id="${user.id}">Eliminar</button>`;
        
        $tbody.append(`
            <tr class="${rowClass}">
                <td class="px-6 py-4">${user.name}</td>
                <td class="px-6 py-4">${user.username}</td>
                <td class="px-6 py-4">${user.dui}</td>
                <td class="px-6 py-4">${roleName}</td>
                <td class="px-6 py-4">${formatDate(user.createdAt)}</td>
                <td class="px-6 py-4 text-center">
                    ${actionButtons}
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
            const user = await $.ajax({
                url: `${apiEndpoint}/${id}`,
                headers: { 'Authorization': `Bearer ${userToken}` }
            });
            openModal(true, user);
        } catch (err) {
            alert('No se pudo cargar la información del usuario');
        }
    });
    $('.delete-btn').off('click').on('click', async function() {
        const id = $(this).data('id');
        if (!confirm('¿Seguro que deseas eliminar este usuario?')) return;
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
            fetchUsers();
        } catch (err) {
            alert('No se pudo eliminar el usuario');
        }
    });
}

$('#btn-add-user').on('click', () => openModal(false));
$('#close-modal, #cancel-modal').on('click', closeModal);
$(window).on('click', (e) => {
    if (e.target === $('#user-modal')[0]) closeModal();
});

$('#user-form').on('submit', async function(e) {
    e.preventDefault();
    try {
        const id = $('#user-id').val();
        const user = {
            name: $('#user-name').val(),
            username: $('#user-username').val(),
            dui: $('#user-dui').val(),
            role: parseInt($('#user-role').val())
        };
        
        // Solo añadir password si no está vacío
        const password = $('#user-password').val();
        if (password) {
            user.password = password;
        }
        
        const userToken = await getTokenRequest();
        if (!userToken) {
            alert('Error de autenticación');
            return;
        }
        
        await $.ajax({
            url: editing && id ? `${apiEndpoint}/${id}` : apiEndpoint,
            method: editing && id ? 'PATCH' : 'POST',
            headers: { 'Authorization': `Bearer ${userToken}` },
            contentType: 'application/json',
            data: JSON.stringify(user)
        });
        
        closeModal();
        fetchUsers();
    } catch (err) {
        alert('Error al guardar el usuario');
    }
});

// Al cargar la página, intentamos obtener el usuario actual de thymeleaf
$(document).ready(function() {
    // Obtener el ID del usuario de la sesión desde el modelo de Thymeleaf
    const sessionUserId = $('#session-user-id').val();
    if (sessionUserId) {
        currentUser = { id: parseInt(sessionUserId) };
    }
    
    fetchUsers();
}); 