function deleteOrderById(id)
{
    let deleteOrderForm = document.getElementById('delete-order-form');
    let idInput = document.createElement("input");
    
    idInput.setAttribute('type', 'hidden');
    idInput.setAttribute('name', 'order_id');
    idInput.setAttribute('value', id);
    
    deleteOrderForm.appendChild(idInput);
    
    deleteOrderForm.submit();
}