function limitNumberOfUploadedFiles(inputForm, amount)
{
    if (inputForm.files.length > amount)
    {
        alert('You uploaded more than 10 files');
        document.getElementById('image-label').innerHTML = 'Choose images (max is 10)';
        document.getElementById("image-label").style.color = "gray";
        inputForm.value = '';
    }
}