function fetchCurrentUser() {
    console.log('Fetching current user info...');
    fetch('/admin/authUser')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch current user info');
            }
            return response.json();
        })
        .then(user => {
            console.log('Current user fetched:', user);
            const emailSpan = document.getElementById('currentUserEmail');
            const roleSpan = document.getElementById('currentUserRole');

            emailSpan.textContent = user.email;
            roleSpan.textContent = user.roles.map(role => role.name.replace('ROLE_', '')).join(', ');
        })
        .catch(error => {
            console.error('Error fetching current user info:', error);
        });
}
