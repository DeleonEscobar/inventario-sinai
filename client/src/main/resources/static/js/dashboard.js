import { getTokenRequest } from '/js/utils/getTokenRequest.js';

$(document).ready(function() {
    loadDashboardData();
    // Actualizar datos cada 1 minuto
    setInterval(loadDashboardData, 1 * 60 * 1000);
});

async function loadDashboardData() {
    try {
        const userToken = await getTokenRequest();
        if (!userToken) {
            console.error('No se pudo obtener el token');
            return;
        }
        
        const apiEndpoint = 'http://localhost:8081/api';
        const headers = { 'Authorization': `Bearer ${userToken}` };

        // Obtener todos los datos en paralelo para mayor eficiencia
        const dashboardTools = await $.ajax({
            url: `${apiEndpoint}/dashboard/boss/${currentUserId}`,
            headers
        });

        // Actualizar contadores
        $('#available-products').text(dashboardTools.totalProductCount);
        $('#pending-movements').text(dashboardTools.totalPendingMovements);
        
        // Actualizar actividades recientes
        const $activitiesList = $('#activities-list').empty();
        
        $.each(dashboardTools.recentMovements, function(i, activity) {
            $('<li>')
                .addClass('mb-4 pb-4 border-b border-slate-200 last:border-b-0')
                .html(`
                    <div class="flex items-start">
                        <div class="flex-grow">
                            <p class="font-semibold">${activity.notes}</p>
                            <p class="text-sm text-gray-500">${new Date(activity.updatedAt).toLocaleString()}</p>
                            <p class="text-xs text-gray-400">Responsable: ${activity.responsibleUser.name}</p>
                        </div>
                    </div>
                `)
                .appendTo($activitiesList);
        });
        
        // Actualizar lotes próximos a vencer
        const $batchesList = $('#batches-expiring-soon').empty();
        
        $.each(dashboardTools.batchesSoonToExpire, function(i, batch) {
            $('<div>')
                .addClass('p-4 bg-red-50 rounded-lg')
                .html(`
                    <div class="flex justify-between items-start">
                        <div>
                            <h4 class="font-semibold">${batch.product.name}</h4>
                            <p class="text-sm text-gray-600">Lote: ${batch.serialNumber}</p>
                            <p class="text-sm text-red-600">Vence: ${new Date(batch.expirationDate).toLocaleDateString()}</p>
                        </div>
                        <div class="text-right">
                            <p class="font-semibold">${batch.amount} unidades</p>
                        </div>
                    </div>
                `)
                .appendTo($batchesList);
        });
        
        // Actualizar lista de empleados
        const $employeesList = $('#active-employees').empty();
        
        if (dashboardTools.recentUsers && dashboardTools.recentUsers.length > 0) {
            $.each(dashboardTools.recentUsers, function(i, employee) {
                // Determinar el tipo de rol
                let roleName = '';
                let roleClass = '';
                
                switch(employee.role) {
                    case 1:
                        roleName = 'Administrador';
                        roleClass = 'bg-blue-100 text-blue-800';
                        break;
                    case 2:
                        roleName = 'Operador';
                        roleClass = 'bg-green-100 text-green-800';
                        break;
                    default:
                        roleName = 'Usuario';
                        roleClass = 'bg-gray-100 text-gray-800';
                }
                
                // Verificar si es el usuario actual
                const isCurrentUser = parseInt(currentUserId) === employee.id;
                const userClass = isCurrentUser ? 'border-blue-300 bg-blue-50' : '';
                
                $('<div>')
                    .addClass(`p-4 rounded-lg border ${userClass} mb-2`)
                    .html(`
                        <div class="flex justify-between items-center">
                            <div>
                                <h4 class="font-semibold">${employee.name}</h4>
                                <p class="text-sm text-gray-600">Usuario: ${employee.username}</p>
                                <p class="text-sm text-gray-600">DUI: ${employee.dui || 'No disponible'}</p>
                            </div>
                            <div>
                                <span class="px-2 py-1 rounded text-xs font-medium ${roleClass}">
                                    ${roleName}
                                </span>
                            </div>
                        </div>
                    `)
                    .appendTo($employeesList);
            });
        } else {
            $employeesList.html('<p class="text-center text-gray-500 py-4">No hay empleados registrados</p>');
        }
        
    } catch (error) {
        console.error('Error al cargar los datos del dashboard:', error);
    }
}