function openEditUserPopup(userId) {
    console.log('Opening edit modal for user ID:', userId);
    fetch(`/admin/users/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user');
            }
            return response.json();
        })
        .then(user => {
            console.log('User fetched for edit:', user);
            document.getElementById('editeId').value = user.id;
            document.getElementById('editeUsername').value = user.username;
            document.getElementById('editeLastName').value = user.lastName;
            document.getElementById('editeAge').value = user.age;
            document.getElementById('editeEmail').value = user.email;
            const editRolesSelect = document.getElementById('editRoles');
            Array.from(editRolesSelect.options).forEach(option => {
                option.selected = user.roles.some(role => role.id === parseInt(option.value, 10));
            });
            openModal('editUser');
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Ошибка при загрузке данных пользователя');
        });
}

document.getElementById('editUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const rolesSelected = Array.from(document.getElementById('editRoles').selectedOptions).map(option => ({
        id: parseInt(option.value, 10)
    }));
    const user = {
        id: parseInt(formData.get('id'), 10),
        username: formData.get('username'),
        lastName: formData.get('lastName'),
        age: formData.get('age'),
        email: formData.get('email'),
        password: formData.get('password'),
        roles: rolesSelected
    };
    console.log('Updating user:', user);
    fetch(`/admin/update`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {
                fetchUsers();
                this.reset();
                closeModal('editUser');
                const tabTrigger = new bootstrap.Tab(document.querySelector('#nav-home-tab'));
                tabTrigger.show();
            } else {
                return response.json().then(errors => {
                    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
                    Object.keys(errors).forEach(field => {
                        const errorElement = document.getElementById(field + '2-error');
                        if (errorElement) {
                            errorElement.textContent = errors[field];
                        }
                    });
                    throw new Error('Validation failed');
                });
            }
        })
        .catch(error => {
            console.error('Error updating user:', error);
            if (error.errors) {
                Object.keys(error.errors).forEach(field => {
                    const errorElement = document.getElementById(field + '2-error');
                    if (errorElement) {
                        errorElement.textContent = error.errors[field];
                    }
                });
            }
        });
});

document.querySelector('#editUserForm button[type="submit"]').addEventListener('click', function () {
    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
});
