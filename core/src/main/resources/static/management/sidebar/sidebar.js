/**
 * Function that transform menu.
 */

function minimize() {
    var menu = document.getElementsByClassName("menuVertical")[0];
    var mainContWidth = document.getElementsByClassName("main-content")[0];
    menu.classList.toggle("narrow");
    var arrow = document.getElementById("maximize");
    if (menu.classList.contains("narrow")) {
        localStorage.setItem("narrow", "narrow");
        for (var u = 0; u < acc.length; u++) {
            var pr = acc[u].nextElementSibling;
            pr.style.display = "none";
            sessionStorage.removeItem("panel" + u);
        }
    } else {
        localStorage.removeItem("narrow");
    }
    if (menu.classList.contains("narrow")) {
        if (!arrow.classList.contains("on")) {
            arrow.classList.add("on");
        }
    } else if (arrow.classList.contains("on")) {
        arrow.classList.remove("on");
    }
    if (arrow.classList.contains("on")) {
        localStorage.setItem("on", "on");
    } else {
        localStorage.removeItem("on");
    }

    mainContWidth.style.paddingLeft = parseInt(window.getComputedStyle(menu).width, 10) + "px";
}

/**
 * Script to check and resize menu on minimize button
 * or resize screen.
 */

var btn = document.getElementById("maximize");

btn.addEventListener("click", minimize);
window.addEventListener("resize", resizeMenu);

function resizeMenu() {
    var wth = window.innerWidth;
    var menu = document.getElementsByClassName("menuVertical")[0];
    if (wth > 1150) {
        if (menu.classList.contains("narrow")) {
            minimize();
        }
    }
    if (wth < 1150) {
        if (!menu.classList.contains("narrow")) {
            minimize();
        }
    }

}

if (localStorage.getItem("narrow") !== null) {
    var menu = document.getElementsByClassName("menuVertical")[0];
    menu.classList.toggle("narrow")
}

if (localStorage.getItem("on") !== null) {
    var minbtn = document.getElementById("maximize");
    minbtn.classList.add("on")
}

/**
 * Script for opening and closing dropdown items.
 */
var acc = document.getElementsByClassName("accordion");

for (var i = 0; i < acc.length; i++) {
    acc[i].addEventListener("click", function () {
        var menu = document.getElementsByClassName("menuVertical")[0];

        var x;
        for (var k = 0; k < acc.length; k++) {
            if (acc[k] !== this) {
                var prev = acc[k].nextElementSibling;
                prev.style.display = "none";
                sessionStorage.removeItem("panel" + k);
            } else {
                x = k;
            }
        }

        var panel = this.nextElementSibling;

        if (menu.classList.contains("narrow")) {
            minimize();
        }
            if (panel.style.display === "block") {
                panel.style.display = "none";
                console.log("will remove storage panel" +x)
                sessionStorage.removeItem("panel" + x);
            } else {
                panel.style.display = "block";
                sessionStorage.setItem("panel" + x, "block");
                console.log("will set storage")
            }
    });
}

var openedPanel = document.getElementsByClassName("panel");
for (var b = 0; b < openedPanel.length; b++) {
    if (sessionStorage.getItem("panel" + b) !== null) {
        openedPanel[b].setAttribute("style", "display: " + sessionStorage.getItem("panel" + b));
    }
}

/**
 * Script for adjusting sidebar height according to height of current page.
 */
window.addEventListener('load', function () {
    var containerH = document.getElementsByClassName("main-content");
    var menuH = document.getElementsByClassName("menuVertical");

    var height = window.getComputedStyle(containerH[0]).height
    var padding = window.getComputedStyle(containerH[0]).paddingTop;
    menuH[0].style.height = parseInt(height, 10) - parseInt(padding, 10) + 'px';
});


/**
 * Script for keeping menu item active
 * when we are transferred by one of links inside this dropdown.
 */

var url = document.URL;
var el = document.getElementById("sidebar");
var links = el.getElementsByTagName("a")

for (var l = 0; l < links.length; l++) {
    if (url.endsWith(links[l].getAttribute("href"))) {
        links[l].classList.toggle("active_li");
    }
}
