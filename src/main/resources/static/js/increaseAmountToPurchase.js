function increaseAmountToPurchase(increaseButton)
{
    let orderDiv = increaseButton.parentElement;
    
    let storedAmount;
    let amountToPurchaseLabel;
    let amountToPurchaseField;
    
    for (let i = 0; i < orderDiv.children.length; i++)
    {
        if (orderDiv.children[i].classList.contains('stored-amount'))
        {
            storedAmount = orderDiv.children[i];
        }
        if (orderDiv.children[i].classList.contains('amount-to-purchase-label'))
        {
            amountToPurchaseLabel = orderDiv.children[i];
        }
        if (orderDiv.children[i].classList.contains('amount-to-purchase-field'))
        {
            amountToPurchaseField = orderDiv.children[i];
        }
    }
    
    if (Number.parseInt(amountToPurchaseField.value) < Number.parseInt(storedAmount.innerHTML))
    {
        amountToPurchaseLabel.innerHTML++;
        amountToPurchaseField.value++;
    }
}