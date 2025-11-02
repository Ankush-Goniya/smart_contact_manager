// console.log("Script loaded")

// let currentTheme = getTheme(); // get saved theme
// changeTheme(); // apply initial theme

// function changeTheme() {
//     // set initial theme
//     document.querySelector("html").classList.add(currentTheme);

//     // set listener
//     const changeThemeButton = document.querySelector('#dark-toggle');
//     changeThemeButton.addEventListener("click", () => {
//         console.log("Change theme button clicked");

//         const oldTheme = currentTheme;

//         // toggle theme
//         currentTheme = (currentTheme === "dark") ? "light" : "dark";

//         // update localStorage
//         setTheme(currentTheme);

//         // update HTML class
//         document.querySelector("html").classList.remove(oldTheme);
//         document.querySelector("html").classList.add(currentTheme);

//         //change the txt of btn
//         changeThemeButton.querySelector("span").textContent=
//         currentTheme=="light"?"dark":"light";
//     });
// }

// // save theme to localStorage
// function setTheme(theme) {
//     localStorage.setItem("theme", theme);
// }

// // get theme from localStorage
// function getTheme() {
//     let theme = localStorage.getItem("theme");
//     return theme ? theme : "light";
// }





console.log("Script loaded");

let currentTheme = getTheme();
changeTheme();

function changeTheme() {
    const html = document.querySelector("html");
    html.classList.remove("light", "dark");
    html.classList.add(currentTheme);

    const changeThemeButton = document.querySelector('#dark-toggle');
    if (!changeThemeButton) {
        console.error("Dark toggle button not found!");
        return;
    }

    changeThemeButton.querySelector("span").textContent =
        currentTheme === "light" ? "dark" : "light";

    changeThemeButton.addEventListener("click", () => {
        console.log("Change theme button clicked");

        const oldTheme = currentTheme;
        currentTheme = (currentTheme === "dark") ? "light" : "dark";

        setTheme(currentTheme);

        html.classList.remove(oldTheme);
        html.classList.add(currentTheme);

        changeThemeButton.querySelector("span").textContent =
            currentTheme === "light" ? "dark" : "light";
    });
}

function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

function getTheme() {
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light";
}

