function resetAddingForm()
{
    let logoInputLabel = document.getElementById('logo-input-label');
    logoInputLabel.innerHTML = 'Choose an image file';
    logoInputLabel.style.color = "gray";
    
    let logoInput = document.getElementById('logo-input');
    logoInput.value = '';
    
    let imagesInputLabel = document.getElementById('images-input-label');
    imagesInputLabel.innerText = 'Choose images';
    logoInputLabel.style.color = 'gray';
    
    let imagesInput = document.getElementById('images-input');
    imagesInput.value = '';
}