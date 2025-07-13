import {initCsrf, getCsrfHeader} from "./csrf.js";

let userIdToDelete = null;
let allUsers = [];


document.addEventListener("DOMContentLoaded", async () => {
    await initCsrf();
    initAll();
});


function initAll() {
    loadUsersTable();
    initNewUserForm();
    loadCurrentUserInfo();
    initEditUserForm();
    initDeleteButton();
}

function initDeleteButton() {
    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
    confirmDeleteBtn.addEventListener("click", () => {
        if (userIdToDelete === null) return;

        fetch(`/api/admin/users/${userIdToDelete}`, {
            method: "DELETE",
            headers: getCsrfHeader(),
            credentials: "same-origin"
        })
            .then(response => {
                if (response.ok) {
                    loadUsersTable();
                    bootstrap.Modal.getInstance(document.getElementById("deleteUserModal")).hide();
                    userIdToDelete = null;
                } else {
                    return response.json().then(err => {
                        throw new Error(err.error || "Failed to delete user");
                    });
                }
            })
            .catch(error => alert("Error: " + error.message));
    });
}

function loadUsersTable() {
    fetch("/api/admin/users")
        .then(response => response.json())
        .then(users => {
            allUsers = users;
            const tbody = document.querySelector("#users-table-body");
            tbody.innerHTML = "";
            users.forEach(user => {
                const roles = user.roles.join(", ");
                const row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                        <td><button class="btn btn-info btn-sm" onclick="openEditModal(${user.id})">Edit</button></td>
                        <td><button class="btn btn-danger btn-sm" onclick="openDeleteModal(${user.id})">Delete</button></td>
                    </tr>`;
                tbody.insertAdjacentHTML("beforeend", row);
            });
        });
}

function loadRolesIntoSelect(selectElement, selectedRoles = []) {
    fetch("/api/admin/roles")
        .then(response => response.json())
        .then(roleNames => {
            selectElement.innerHTML = "";
            roleNames.forEach(role => {
                const opt = document.createElement("option");
                opt.value = role;
                opt.textContent = role;
                if (selectedRoles.includes(role)) opt.selected = true;
                selectElement.appendChild(opt);
            });
        });
}

function openDeleteModal(userId) {
    userIdToDelete = userId;
    const user = allUsers.find(u => u.id === userId);
    if (!user) return;

    document.getElementById("del-id").value = user.id;
    document.getElementById("del-firstname").value = user.firstName;
    document.getElementById("del-lastname").value = user.lastName;
    document.getElementById("del-age").value = user.age;
    document.getElementById("del-email").value = user.email;
    loadRolesIntoSelect(document.getElementById("del-roles"), user.roles);

    new bootstrap.Modal(document.getElementById("deleteUserModal")).show();
}

function openEditModal(userId) {
    fetch(`/api/admin/users`)
        .then(response => response.json())
        .then(users => {
            const user = users.find(u => u.id === userId);
            if (!user) return;

            document.getElementById("edit-id").value = user.id;
            document.getElementById("edit-first-name").value = user.firstName;
            document.getElementById("edit-last-name").value = user.lastName;
            document.getElementById("edit-age").value = user.age;
            document.getElementById("edit-email").value = user.email;
            document.getElementById("edit-password").value = "";
            loadRolesIntoSelect(document.getElementById("edit-roles"), user.roles);
            new bootstrap.Modal(document.getElementById("editUserModal")).show();
        });
}

function initNewUserForm() {
    const form = document.getElementById("new-user-form");
    const rolesSelect = form.querySelector('select[name="roles"]');
    loadRolesIntoSelect(rolesSelect);

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        const formData = new FormData(form);
        const roles = Array.from(rolesSelect.selectedOptions).map(opt => opt.value);

        const userDto = {
            firstName: formData.get("first_name"),
            lastName: formData.get("last_name"),
            age: parseInt(formData.get("age")),
            email: formData.get("email"),
            roles: roles
        };

        const userRequest = {
            userDto: userDto,
            password: formData.get("password")
        };

        fetch("/api/admin/users", {
            method: "POST",
            headers: getCsrfHeader(),
            body: JSON.stringify(userRequest),
            credentials: 'same-origin'
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(msg => {
                        throw new Error(msg);
                    });
                }
                return response.json();
            })
            .then(() => {
                form.reset();
                loadUsersTable();
                document.querySelector("#users-tab").click();
            })
            .catch(error => alert("Error: " + error.message));
    });
}

function initEditUserForm() {
    document.getElementById("edit-user-form").addEventListener("submit",
        function (event) {
        event.preventDefault();

        const roles = Array.from(document.getElementById("edit-roles")
            .selectedOptions).map(o => o.value);
        const userDto = {
            id: parseInt(document.getElementById("edit-id").value),
            firstName: document.getElementById("edit-first-name").value,
            lastName: document.getElementById("edit-last-name").value,
            age: parseInt(document.getElementById("edit-age").value),
            email: document.getElementById("edit-email").value,
            roles: roles
        };

        const userRequest = {
            userDto: userDto,
            password: document.getElementById("edit-password").value
        };

        fetch("/api/admin/users", {
            method: "PUT",
            headers: getCsrfHeader(),
            body: JSON.stringify(userRequest),
            credentials: 'same-origin'
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(msg => {
                        throw new Error(msg);
                    });
                }
                return response.json();
            })
            .then(() => {
                loadUsersTable();
                bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
            })
            .catch(error => alert("Error: " + error.message));
    });
}

function loadCurrentUserInfo() {
    fetch("/api/admin/me")
        .then(res => res.json())
        .then(user => {
            document.getElementById("navbar-email").textContent = user.email;
            document.getElementById("navbar-roles").textContent = user.roles.map(r =>
                r.toLowerCase()).join(" ");

            const tbody = document.getElementById("user-info-body");
            tbody.innerHTML = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.join(", ")}</td>
                </tr>
            `;
        });
}

window.openEditModal = openEditModal;
window.openDeleteModal = openDeleteModal;