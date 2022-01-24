function checkIfAllItemsInStockAndSendForm()
{
    let orders = document.getElementsByClassName('stored-amount');
    let purchaseForm = document.getElementById('purchase-form');
    
    for (let i = 0; i < orders.length; i++)
    {
        if (orders[i].innerHTML === '0')
        {
            alert('some of ordered items is out of stock, please delete it');
            return;
        }
    }
    
    purchaseForm.submit();
}