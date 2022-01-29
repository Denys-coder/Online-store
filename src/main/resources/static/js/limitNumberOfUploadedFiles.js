function limitNumberOfUploadedFiles(inputForm, amount)
{
    if (inputForm.files.length > amount)
    {
        alert('You uploaded more than 10 files');
        document.getElementById('images-input-label').innerHTML = 'Choose images';
        document.getElementById("images-input-label").style.color = "gray";
        inputForm.value = '';
    }
}