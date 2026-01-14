document.addEventListener("DOMContentLoaded", function () {
    fetch('user/profile_user')
        .then(response => response.json())
        .then(user => {
            document.getElementById("id").textContent = user.id;
            document.getElementById("username").textContent = user.username;
            document.getElementById("lastName").textContent = user.lastName;
            document.getElementById("age").textContent = user.age;
            document.getElementById("email").textContent = user.email;
            let roles = user.authorities.map(role => role.authority.substring(5)).join(", ");
            document.getElementById("roles").textContent = roles;
            document.getElementById("navbarUserEmail").textContent = user.email;
            document.getElementById("navbarUserRoles").textContent = roles;
            const adminNavItem = document.getElementById('adminNavItem');
            if (!user.authorities.some(role => role.authority === 'ROLE_ADMIN')) {
                adminNavItem.style.display = 'none';
            }
        })
        .catch(error => console.error("Error fetching user data:", error));
});