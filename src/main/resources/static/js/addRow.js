function addRow()
{
    let specNameField = document.createElement("input");
    specNameField.setAttribute("type", "text");
    specNameField.setAttribute("name", "spec-names");
    specNameField.setAttribute("placeholder", "spec name");
    
    let specValueField = document.createElement("input");
    specValueField.setAttribute("type", "text");
    specValueField.setAttribute("name", "spec-values");
    specValueField.setAttribute("placeholder", "spec value");
    
    let deleteRowButton = document.createElement("button");
    deleteRowButton.setAttribute("type", "button");
    deleteRowButton.setAttribute("onclick", "deleteRow(this)");
    deleteRowButton.innerHTML = "-";
    
    let breakLine = document.createElement("br");
    
    let row = document.createElement("div");
    row.appendChild(specNameField);
    row.appendChild (document.createTextNode (" "));
    row.appendChild(specValueField);
    row.appendChild (document.createTextNode (" "));
    row.appendChild(deleteRowButton);
    row.appendChild (document.createTextNode (" "));
    row.appendChild(breakLine);
    
    let specsForm = document.getElementById('specsForm');
    specsForm.appendChild(row);
}