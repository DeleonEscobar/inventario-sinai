// Archivo intencionalmente vacío: la navegación y creación de movimientos ahora es manejada por Thymeleaf y el backend.

import { getTokenRequest } from '/js/utils/getTokenRequest.js';
import { formatDate } from '/js/utils/formatDate.js';

const apiEndpoint = 'http://localhost:8081/api/movements';

$(document).ready(function() {
    // Inicializar la tabla de movimientos
    fetchMovements();

    function fetchMovements() {
        try {
            $('#movements-table-body').html('<tr><td colspan="7" class="text-center py-6 text-slate-400">Cargando...</td></tr>');
            
            // Obtener los movimientos desde el servidor
            $.ajax({
                url: '/dashboard/admin/movements/api/list',
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
            $tbody.append(`
                <tr>
                    <td class="px-6 py-4">${movement.name}</td>
                    <td class="px-6 py-4">${movement.responsible.name}</td>
                    <td class="px-6 py-4">${movement.client.name}</td>
                    <td class="px-6 py-4">${movement.status}</td>
                    <td class="px-6 py-4">${movement.batchCount || 0}</td>
                    <td class="px-6 py-4">${formatDate(movement.createdAt)}</td>
                    <td class="px-6 py-4 text-center">
                        <a href="/dashboard/admin/movements/${movement.id}" class="view-btn text-blue-600 hover:underline mr-2">Ver</a>
                        <a href="/dashboard/admin/movements/edit/${movement.id}" class="edit-btn text-blue-600 hover:underline mr-2">Editar</a>
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
