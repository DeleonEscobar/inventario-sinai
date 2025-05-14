export async function getTokenRequest() {
    let endpoint = 'http://localhost:8080';

    try {
        const response = await fetch(`${endpoint}/token`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('No se pudo obtener el token');
        }

        const data = await response.json();
        return data.token; 
    } catch (error) {
        console.error('Error al obtener el token:', error);
        return null;
    }
}
