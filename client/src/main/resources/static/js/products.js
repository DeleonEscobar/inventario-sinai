import { getTokenRequest } from '/js/utils/getTokenRequest.js';

// Variables globales
const apiEndpoint = 'http://localhost:8081/api/products';
const tableBody = document.getElementById('products-table-body');
const btnAddProduct = document.getElementById('btn-add-product');
const modal = document.getElementById('product-modal');
const closeModalBtn = document.getElementById('close-modal');
const cancelModalBtn = document.getElementById('cancel-modal');
const modalTitle = document.getElementById('modal-title');
const productForm = document.getElementById('product-form');

const inputId = document.getElementById('product-id');
const inputName = document.getElementById('product-name');
const inputDescription = document.getElementById('product-description');
const inputPrice = document.getElementById('product-price');
const inputStock = document.getElementById('product-stock');

let editing = false;

// Función para abrir el modal (para crear o editar)
function openModal(edit = false, product = null) {
    modal.classList.remove('hidden');
    editing = edit;
    if (edit && product) {
        modalTitle.textContent = 'Editar Producto';
        inputId.value = product.id;
        inputName.value = product.name;
        inputDescription.value = product.description;
        inputPrice.value = product.price;
        inputStock.value = product.stock;
    } else {
        modalTitle.textContent = 'Agregar Producto';
        productForm.reset();
        inputId.value = '';
    }
}

// Función para cerrar el modal
function closeModal() {
    modal.classList.add('hidden');
    productForm.reset();
    inputId.value = '';
}

// Event listeners para el modal
btnAddProduct.addEventListener('click', () => openModal(false));
closeModalBtn.addEventListener('click', closeModal);
cancelModalBtn.addEventListener('click', closeModal);

window.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
});

// Función principal para cargar productos
async function fetchProducts() {
    try {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center py-6 text-slate-400">Cargando...</td></tr>';

        // Obtener token de autenticación
        let userToken = await getTokenRequest();

        if (!userToken) {
            console.error('No se pudo obtener el token');
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center py-6 text-red-500">Error de autenticación</td></tr>';
            return;
        }

        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };

        const res = await fetch(apiEndpoint, {
            headers
        });

        if (!res.ok) {
            throw new Error(`Error HTTP: ${res.status}`);
        }

        const products = await res.json();
        renderProducts(products);
    } catch (err) {
        console.error('Error al cargar productos:', err);
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center py-6 text-red-500">Error al cargar productos</td></tr>';
    }
}

// Función para renderizar la tabla de productos
function renderProducts(products) {
    if (!products || !products.length) {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center py-6 text-slate-400">No hay productos registrados</td></tr>';
        return;
    }

    tableBody.innerHTML = '';
    products.forEach(product => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="px-6 py-4">${product.name}</td>
            <td class="px-6 py-4">${product.description}</td>
            <td class="px-6 py-4">$${Number(product.price).toFixed(2)}</td>
            <td class="px-6 py-4">${product.stock}</td>
            <td class="px-6 py-4 text-center">
                <button class="edit-btn text-blue-600 hover:underline mr-2" data-id="${product.id}">Editar</button>
                <button class="delete-btn text-red-600 hover:underline" data-id="${product.id}">Eliminar</button>
            </td>
        `;
        tableBody.appendChild(tr);
    });

    // Asignar eventos a los botones
    assignButtonEvents();
}

// Función para asignar eventos a los botones de editar y eliminar
async function assignButtonEvents() {
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            try {
                const id = e.target.getAttribute('data-id');

                // Obtener token de autenticación
                let userToken = await getTokenRequest();

                if (!userToken) {
                    console.error('No se pudo obtener el token');
                    alert('Error de autenticación');
                    return;
                }

                const headers = {
                    'Authorization': `Bearer ${userToken}`,
                    'Content-Type': 'application/json'
                };

                const res = await fetch(`${apiEndpoint}/${id}`, {
                    headers
                });

                if (!res.ok) {
                    throw new Error(`Error HTTP: ${res.status}`);
                }

                const product = await res.json();
                openModal(true, product);
            } catch (err) {
                console.error('Error al obtener detalles del producto:', err);
                alert('No se pudo cargar la información del producto');
            }
        });
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            try {
                const id = e.target.getAttribute('data-id');
                if (confirm('¿Seguro que deseas eliminar este producto?')) {
                    // Obtener token de autenticación
                    let userToken = await getTokenRequest();

                    if (!userToken) {
                        console.error('No se pudo obtener el token');
                        alert('Error de autenticación');
                        return;
                    }

                    const headers = {
                        'Authorization': `Bearer ${userToken}`,
                        'Content-Type': 'application/json'
                    };

                    const res = await fetch(`${apiEndpoint}/${id}`, {
                        method: 'DELETE',
                        headers
                    });

                    if (!res.ok) {
                        throw new Error(`Error HTTP: ${res.status}`);
                    }

                    fetchProducts();
                }
            } catch (err) {
                console.error('Error al eliminar el producto:', err);
                alert('No se pudo eliminar el producto');
            }
        });
    });
}

// Event listener para el formulario
productForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const id = inputId.value;
        const product = {
            name: inputName.value,
            description: inputDescription.value,
            price: parseFloat(inputPrice.value),
            stock: parseInt(inputStock.value)
        };

        // Obtener token de autenticación
        let userToken = await getTokenRequest();

        if (!userToken) {
            console.error('No se pudo obtener el token');
            alert('Error de autenticación');
            return;
        }

        const headers = {
            'Authorization': `Bearer ${userToken}`,
            'Content-Type': 'application/json'
        };

        let res;
        if (editing && id) {
            res = await fetch(`${apiEndpoint}/${id}`, {
                method: 'PUT',
                headers,
                body: JSON.stringify(product)
            });
        } else {
            res = await fetch(apiEndpoint, {
                method: 'POST',
                headers,
                body: JSON.stringify(product)
            });
        }

        if (!res.ok) {
            throw new Error(`Error HTTP: ${res.status}`);
        }

        closeModal();
        fetchProducts();
    } catch (err) {
        console.error('Error al guardar el producto:', err);
        alert('Error al guardar el producto');
    }
});

// Inicializar la carga de productos
if (document.readyState === 'loading') {
    // Si aún no está listo, esperar al evento DOMContentLoaded
    document.addEventListener('DOMContentLoaded', () => {
        fetchProducts();
    });
} else {
    // Si ya está listo, ejecutar inmediatamente
    fetchProducts();
}