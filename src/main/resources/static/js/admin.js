let userIdToDelete = null;
let allUsers = [];

document.addEventListener("DOMContentLoaded", () => {
    loadUsersTable();
    initNewUserForm();

    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
    confirmDeleteBtn.addEventListener("click", () => {
        if (userIdToDelete === null) return;

        const csrfToken = document.querySelector('meta[name="_csrf"]')
            .getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')
            .getAttribute('content');

        fetch(`/api/admin/users/${userIdToDelete}`, {
            method: "DELETE",
            headers: {
                [csrfHeader]: csrfToken
            }
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
            .catch(error => {
                alert("Error: " + error.message);
            });
    });
});

function openDeleteModal(userId) {
    userIdToDelete = userId;
    const user = allUsers.find(u => u.id === userId);
    if (!user) {
        alert("User not found");
        return;
    }

    document.getElementById("del-id").value = user.id;
    document.getElementById("del-firstname").value = user.firstName;
    document.getElementById("del-lastname").value = user.lastName;
    document.getElementById("del-age").value = user.age;
    document.getElementById("del-email").value = user.email;

    const roleSelect = document.getElementById("del-roles");
    roleSelect.innerHTML = "";
    user.roles.forEach(role => {
        const opt = document.createElement("option");
        opt.textContent = role;
        opt.selected = true;
        roleSelect.appendChild(opt);
    });

    const deleteModal = new bootstrap.Modal(document.getElementById("deleteUserModal"));
    deleteModal.show();
}


function loadUsersTable() {
    fetch("/api/admin/users")
        .then(response => response.json())
        .then(users => {
            allUsers = users
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
                        <td>
                            <button class="btn btn-info btn-sm" onclick="openEditModal(${user.id})">Edit</button>
                        </td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="openDeleteModal(${user.id})">Delete</button>
                        </td>
                    </tr>
                `;
                tbody.insertAdjacentHTML("beforeend", row);
            });
        });
}

function initNewUserForm() {
    const form = document.getElementById("new-user-form");
    if (!form) return;

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);

        const roles = Array.from(
            document.querySelector('select[name="roles"]').selectedOptions
        ).map(opt => opt.value);

        const userDto = {
            firstName: formData.get("first_name"),
            lastName: formData.get("last_name"),
            age: parseInt(formData.get("age")),
            email: formData.get("email"),
            roles: roles
        };

        const password = formData.get("password");

        const userRequest = {
            userDto: userDto,
            password: password
        };

        const csrfToken = document.querySelector('meta[name="_csrf"]')
            .getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')
            .getAttribute('content');

        fetch("/api/admin/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(userRequest)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.json().then(err => {
                        throw new Error(err.error || "Unknown error");
                    });
                }
            })
            .then(data => {
                form.reset();
                loadUsersTable();
                document.querySelector("#users-tab").click();
            })
            .catch(error => {
                alert("Error: " + error.message);
            });
    });

}

function openEditModal(userId) {
    fetch(`/api/admin/users`)
        .then(response => response.json())
        .then(users => {
            const user = users.find(u => u.id === userId);
            if (!user) return;

            // Заполнение полей
            document.getElementById("edit-id").value = user.id;
            document.getElementById("edit-first-name").value = user.firstName;
            document.getElementById("edit-last-name").value = user.lastName;
            document.getElementById("edit-age").value = user.age;
            document.getElementById("edit-email").value = user.email;
            document.getElementById("edit-password").value = "";

            const roleSelect = document.getElementById("edit-roles");
            Array.from(roleSelect.options).forEach(option => {
                option.selected = user.roles.includes(option.value);
            });

            const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
            modal.show();
        });
}

document.getElementById("edit-user-form").addEventListener("submit",
    function (event) {
        event.preventDefault();

        const roles = Array.from(document.getElementById("edit-roles").selectedOptions)
            .map(o => o.value);
        const userDto = {
            id: parseInt(document.getElementById("edit-id").value),
            firstName: document.getElementById("edit-first-name").value,
            lastName: document.getElementById("edit-last-name").value,
            age: parseInt(document.getElementById("edit-age").value),
            email: document.getElementById("edit-email").value,
            roles: roles
        };
        const password = document.getElementById("edit-password").value;

        const userRequest = {
            userDto: userDto,
            password: password
        };
        const csrfToken = document.querySelector('meta[name="_csrf"]')
            .getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')
            .getAttribute('content');

        fetch("/api/admin/users", {
            method: "PUT",
            headers: {"Content-Type": "application/json", [csrfHeader]: csrfToken},
            body: JSON.stringify(userRequest)
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update user");
                return response.json();
            })
            .then(data => {
                loadUsersTable();
                bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
            })
            .catch(error => alert("Error: " + error.message));
    });
