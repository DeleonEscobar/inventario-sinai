// Archivo intencionalmente vacío: la navegación y creación de movimientos ahora es manejada por Thymeleaf y el backend.

import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/movements';
let isEmployee = false;

$(document).ready(function() {
    // Detectar si el usuario es empleado
    const userData = $('#user-data');
    if (userData.length > 0) {
        isEmployee = userData.data('user-role') === 'EMPLOYEE';
    }

    console.log(isEmployee);

    // Inicializar la tabla de movimientos
    fetchMovements();

    function fetchMovements() {
        try {
            $('#movements-table-body').html('<tr><td colspan="7" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
            
            // Obtener los movimientos desde el servidor
            $.ajax({
                url: isEmployee ? '/dashboard/employee/movements/api/list' : '/dashboard/admin/movements/api/list',
                method: 'GET',
                dataType: 'json',
                success: function(movements) {
                    renderMovements(movements);
                },
                error: function(error) {
                    $('#movements-table-body').html('<tr><td colspan="7" class="text-center py-6 text-red-500">Error al cargar movimientos</td></tr>');
                    console.error('Error al cargar los movimientos:', error);
                }
            });
        } catch (err) {
            $('#movements-table-body').html('<tr><td colspan="7" class="text-center py-6 text-red-500">Error al cargar movimientos</td></tr>');
        }
    }

    function renderMovements(movements) {
        const $tbody = $('#movements-table-body');
        if (!movements || !movements.length) {
            $tbody.html('<tr><td colspan="7" class="text-center py-6 text-slate-400">No hay movimientos registrados</td></tr>');
            return;
        }
        
        $tbody.empty();
        $.each(movements, function(i, movement) {
            // Determinar los botones a mostrar según el rol
            let actionButtons = '';
            
            if (isEmployee) {
                // Si es empleado, solo mostrar el botón de editar con URL específica para empleados
                actionButtons = `<a href="/dashboard/employee/movements/${movement.id}" class="edit-btn text-purple-600 hover:underline">Ver Detalles</a>`;
            } else {
                // Si es admin, mostrar ambos botones con URLs de admin
                actionButtons = `
                    <a href="/dashboard/admin/movements/${movement.id}" class="view-btn text-blue-600 hover:underline mr-2">Ver</a>
                    <a href="/dashboard/admin/movements/${movement.id}/edit" class="edit-btn text-blue-600 hover:underline mr-2">Editar</a>
                `;
            }
            
            $tbody.append(`
                <tr>
                    <td class="px-6 py-4">${movement.notes || ''}</td>
                    <td class="px-6 py-4">${movement.responsibleUser ? movement.responsibleUser.name : ''}</td>
                    <td class="px-6 py-4">${movement.client ? movement.client.name : '-'}</td>
                    <td class="px-6 py-4">
                        <div class="${movement.statusColorString || ''} inline-block px-3 py-1 rounded-full text-sm font-semibold">${movement.statusName || ''}</div>
                    </td>
                    <td class="px-6 py-4">${movement.batches ? movement.batches.length : 0}</td>
                    <td class="px-6 py-4">${formatDate(movement.createdAt)}</td>
                    <td class="px-6 py-4 text-center">
                        ${actionButtons}
                    </td>
                </tr>
            `);
        });
    }

    // Filtros y búsqueda
    $('#search-form').on('submit', function(e) {
        e.preventDefault();
        fetchMovements();
    });

    $('#btn-clear-filters').on('click', function() {
        $('#search-form')[0].reset();
        fetchMovements();
    });
});
