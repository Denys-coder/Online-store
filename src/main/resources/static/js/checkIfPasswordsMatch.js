function checkIfPasswordsMatch()
{
    let password = document.getElementById('new-password');
    let repeatedPassword = document.getElementById('repeated-new-password');
    let passwordForm = document.getElementById('password-form');
    
    if (password.value !== repeatedPassword.value)
    {
        alert('password don\'t match');
        return;
    }
    
    if (password.value.length < 8 || password.value.length > 64)
    {
        alert('password must be 8-64 characters long');
        return;
    }
    
    passwordForm.submit();
}