function decreaseAmountToPurchase(decreaseButton)
{
    let orderDiv = decreaseButton.parentElement;
    
    let amountToPurchaseLabel;
    let amountToPurchaseField;
    
    for (let i = 0; i < orderDiv.children.length; i++)
    {
        if (orderDiv.children[i].classList.contains('amount-to-purchase-label'))
        {
            amountToPurchaseLabel = orderDiv.children[i];
        }
        if (orderDiv.children[i].classList.contains('amount-to-purchase-field'))
        {
            amountToPurchaseField = orderDiv.children[i];
        }
    }
    
    if (Number.parseInt(amountToPurchaseField.value) > 1)
    {
        amountToPurchaseLabel.innerHTML--;
        amountToPurchaseField.value--;
    }
}