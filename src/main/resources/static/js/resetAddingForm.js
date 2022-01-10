function resetAddingForm()
{
    document.getElementById('image-label').innerHTML = 'Choose images (max is 10)';
    document.getElementById("image-label").style.color = "gray";
    document.getElementById('image-input').value = '';
}