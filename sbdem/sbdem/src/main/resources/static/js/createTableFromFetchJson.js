function json2Table(json,parentElement) {
    return new Promise((resolve,reject) =>{
        let rootTable = document.createElement('table');
        rootTable.className = "table-responsive table-bordered table-success";
        let rootTHead = document.createElement('thead');
        rootTable.appendChild(rootTHead);
        let rootTdForTableOfObjects = document.createElement('td');
        // we need to create one tHead for all objects of array
        // beyond the scope of new recursion
        let rootKeySet = new Set();
        //prepare tHead if json root node is object
        if (json && (!Array.isArray(json)) && ( typeof json === 'object')) {
            createHeadHelper(json,rootTable,rootKeySet)
        }
        function createHeadHelper(jsonData, table, keySet) {
            if (jsonData && (!Array.isArray(jsonData)) && (typeof jsonData === 'object')) {
                Object.keys(jsonData).forEach(function(key) {
                    // This function will be called for each found "own property", and
                    // don't need to do the instance.hasOwnProperty check anymore
                    if (!keySet.has(key)) {
                        keySet.add(key); // we have all keys of objects as bonus
                        let thForObject = document.createElement('th');
                        thForObject.appendChild(document.createTextNode(key));
                        table.tHead.appendChild(thForObject);
                    }
                });
            }
        }
        // * we assume that we have more than one object in parent element
        // create common tHead for them
        function createTHead(jsonData, table, keySet) {
            if (Array.isArray(jsonData)) {
                for (let obj of jsonData) {
                    createHeadHelper(obj,table,keySet)
                }
            }
            if (jsonData && (!Array.isArray(jsonData)) && (typeof jsonData === 'object')) {
                Object.keys(jsonData).forEach(function(key) {
                    createHeadHelper(jsonData[key],table,keySet)
                });
            }
        }
        function createTable(json,rootTable,tdForObjectsTable,keySet) {
            return new Promise((resolve,reject) =>{

                let tableForChildElement = document.createElement('table');
                let tHeadForChildElement = document.createElement('thead');
                tableForChildElement.appendChild(tHeadForChildElement);
                // * we need to crate one tHead for all objects of array
                // beyond the scope of new recursion
                let innerTdForTableOfObjects = document.createElement('td');
                let keySetInRecur = new Set();

                createTHead(json,tableForChildElement,keySetInRecur);

                if(Array.isArray(json)){
                    let trForArray = document.createElement('tr');
                    for(let jsonVal of json){
                        // If we won't check again if value of array is array we will insert same value twice.
                        // Once from recursion stack. Once per root stack.
                        if(Array.isArray(jsonVal)) {
                            let tdForArray = document.createElement('td');

                            tdForArray.appendChild(tableForChildElement);
                            try {
                                resolve(createTable(jsonVal, tableForChildElement, innerTdForTableOfObjects, keySetInRecur));
                            } catch (e){
                                reject(e);
                            }
                            trForArray.appendChild(tdForArray);
                        } else if (jsonVal && (!Array.isArray(jsonVal)) && (typeof jsonVal === 'object')){
                            // Structure can be like [ {} {} {} {} ] - many objects in one array.
                            // This way we can share one tHead for all objects *
                            // and put all objects into 1 table
                            tdForObjectsTable.appendChild(tableForChildElement);
                            try{
                                resolve(createTable(jsonVal, tableForChildElement, innerTdForTableOfObjects,keySetInRecur));
                            } catch (e) {
                                reject (e);
                            }
                            trForArray.appendChild(tdForObjectsTable);

                        } else {
                            let tdForArray = document.createElement('td');
                            tdForArray.appendChild(document.createTextNode(jsonVal));
                            trForArray.appendChild(tdForArray);
                        }
                    }
                    //But array do not need tHead. It has no keys
                    //But we NEED tHead BEFORE we know  if we need it, on previous stack of recursion
                    rootTable.removeChild(rootTable.tHead);
                    rootTable.appendChild(trForArray);
                }
                if (json && (!Array.isArray(json)) && ( typeof json === 'object')){
                    let trForObject = document.createElement('tr');
                    //than fill data from objects into the tr's of root table
                    //We iterate over the KEY SET not keys of object filling blank td for missing data
                    for ( let keyOfSet of keySet) {
                        let tdForObject = document.createElement('td');
                        if (json.hasOwnProperty(keyOfSet)) {
                            if (Array.isArray(json[keyOfSet])) {
                                tdForObject.appendChild(tableForChildElement);
                                try{
                                    resolve(createTable(json[keyOfSet], tableForChildElement, innerTdForTableOfObjects, keySetInRecur));
                                } catch (e) {
                                    reject(e);
                                }
                                trForObject.appendChild(tdForObject);

                            } else if (json[keyOfSet] && (!Array.isArray(json[keyOfSet])) && (typeof json[keyOfSet] === 'object')) {
                                tdForObjectsTable.appendChild(tableForChildElement);
                                try{
                                    resolve(createTable(json[keyOfSet], tableForChildElement, innerTdForTableOfObjects, keySetInRecur));
                                } catch (e) {
                                    reject(e);
                                }
                                trForObject.appendChild(tdForObjectsTable);

                            } else {
                                tdForObject.appendChild(document.createTextNode(json[keyOfSet]));
                                trForObject.appendChild(tdForObject);
                            }
                        } else{
                            trForObject.appendChild(tdForObject);
                        }
                    }
                    rootTable.appendChild(trForObject);
                }
            })
        }
        createTable(json,rootTable,rootTdForTableOfObjects,rootKeySet);
        try{
            resolve(rootTable);
        } catch (e) {
            reject(e)
        }
    })
}

function createPostPanel(parentId) {

    let parentElement = document.getElementById(parentId);

    let divForTextArea = document.createElement('div');

    let allFieldsOfUser = {
        spUserId : document.getElementById("spUserId"),
        createdAt : document.getElementById('createdAt'),
        username : document.getElementById('username'),
        password : document.getElementById('password'),
        email : document.getElementById('email'),
        aboutUser : document.getElementById('aboutUser'),
        avatarImgUrl : document.getElementById('avatarImgUrl'),
        spRoleId : document.getElementById('spRoleId'),
        role  : document.getElementById('role'),
        enabled : document.getElementById('enabled'),
        fullName : document.getElementById('fullName'),
        accountNonExpired : document.getElementById('accountNonExpired'),
        accountNonLocked : document.getElementById('accountNonLocked'),
        credentialsNonExpired : document.getElementById('credentialsNonExpired'),
    };

    let createNewUserButton = document.getElementById('createNewUserButton');
    createNewUserButton.addEventListener("click",postUserAsJson);

    let deleteUserButton = document.getElementById('deleteUserButton');
    deleteUserButton.addEventListener("click",deleteUser);

    let fullUpdateButton = document.getElementById('fullUpdateButton');
    fullUpdateButton.addEventListener('click',putUser);

    let patchButton = document.getElementById('patchButton');
    patchButton.addEventListener('click',patchUser);

    parentElement.appendChild(divForTextArea);

    const token =  getCookie("XSRF-TOKEN");
    let headers =  new Headers({
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': token,
    });
    let findUserInput = document.getElementById('findUserInput');
    let findUserButton = document.getElementById('findUserButton');
    findUserButton.addEventListener('click', findUser);


    function getJsonForPatch() {
        return "{\"fullName\":\""+allFieldsOfUser.fullName.value+"\",\"email\":\""+allFieldsOfUser.email.value+"\",\"aboutUser\":\""+allFieldsOfUser.aboutUser.value+"\"}";
    }
    function getJsonForPOST() {
        return "{\"username\":\""+allFieldsOfUser.username.value+"\",\"password\":\""+allFieldsOfUser.password.value+"\",\"matchPassword\":\""+allFieldsOfUser.password.value+"\",\"email\":\""+allFieldsOfUser.email.value+"\"}";
    }
    function getJsonForPUT() {
        return "{\"spUserId\":\""+allFieldsOfUser.spUserId.value+"\",\"createdAt\":\""+allFieldsOfUser.createdAt.value+"\",\"username\":\""+allFieldsOfUser.username.value+"\",\"password\":\""+allFieldsOfUser.password.value+"\",\"email\":\""+allFieldsOfUser.email.value+"\",\"aboutUser\":\""+allFieldsOfUser.aboutUser.value+"\",\"avatarImgUrl\":\""+allFieldsOfUser.avatarImgUrl.value+"\",\"roles\":"+"[{\"spRoleId\":\""+allFieldsOfUser.spRoleId.value+"\",\"role\":\""+allFieldsOfUser.role.value+"\"}]"+",\"enabled\":\""+allFieldsOfUser.avatarImgUrl.value +"\",\"fullname\":\""+allFieldsOfUser.fullName.value +"\",\"accountNonExpired\":\""+allFieldsOfUser.accountNonExpired.value+"\",\"accountNonLocked\":\""+allFieldsOfUser.accountNonLocked.value+"\",\"credentialsNonExpired\":\"" +allFieldsOfUser.credentialsNonExpired.value+"\"}";
    }
    function getCookie(name) {
        if (!document.cookie) {
            return null;
        }
        const xsrfCookies = document.cookie.split(';')
            .map(c => c.trim())
            .filter(c => c.startsWith(name + '='));

        if (xsrfCookies.length === 0) {
            return null;
        }
        return decodeURIComponent(xsrfCookies[0].split('=')[1]);
    }

    function afterFetch(response, parentId) {
        return new Promise((resolve, reject) => {
            try{
                resolve(response.json())
            } catch (e){
                reject(e)
            }
        })
            .then(json => json2Table(json))
            .then(table => {
                let div = document.getElementById(parentId);
                div.replaceChild(table, div.firstChild);
            })
            .then(getAllUsers)
            .catch(err => {
                alert("Fetch error: " + err);
            });
    }
    function findUser(){
        fetch(`admin/api/get-user/`+findUserInput.value, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => json2Table(json))
            .then(table => {
                let div = document.getElementById('findUserDiv');
                div.replaceChild(table, div.firstChild);
            })
            .catch(err => {
                alert("Fetch error: " + err);
            });
    }

    function postUserAsJson() {
        fetch('admin/api/create-user', {
            method: 'POST',
            credentials: 'include',
            headers : headers,
            body: getJsonForPOST() ,
        })
            .then(response => afterFetch(response, "responseFromServer"));
    }
    
    function deleteUser() {
        fetch('admin/api/delete-user/'+allFieldsOfUser.username.value, {
            method: 'DELETE',
            credentials: 'include',
            headers : headers,
        })
            .then(response => afterFetch(response, "responseFromServer"));
    }
    
    function putUser() {
        fetch('admin/api/full-update-user/'+allFieldsOfUser.spUserId.value, {
            method: 'PUT',
            credentials: 'include',
            headers : headers,
            body : getJsonForPUT(),
        })
            .then(response =>  afterFetch(response, "responseFromServer"));
    }
    function patchUser() {
        fetch('admin/api/update-user-info/'+allFieldsOfUser.username.value, {
            method: 'PATCH',
            credentials: 'include',
            headers : headers,
            body : getJsonForPatch(),
        })
            .then(response =>  afterFetch(response, "responseFromServer"));
    }
    function getAllUsers() {
        fetch(`admin/api/get-all-users`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => json2Table(json))
            .then(table =>{
                let div = document.getElementById("forUserTable");
                div.replaceChild(table, div.firstChild);
            })
            .catch(err => {
                let div = document.getElementById("forUserTable");
                div.innerHTML += "fetch error" + err;
            });
    }

    getAllUsers();
}



document.onreadystatechange = function () {
    if (document.readyState === "interactive") {
        createPostPanel("ApiPanel");
    }

};
