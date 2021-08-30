window.onload = function (){
    (function () {


        var boxElem = document.getElementById('table1');
        var pointerElem = document.getElementById('pointer1');

        function onMouseMove(event) {
            var mouseX = event.pageX;
            var mouseY = event.pageY;
            var crd = boxElem.getBoundingClientRect();
            var activePointer = crd.left <= mouseX && mouseX <= crd.right && crd.top <= mouseY && mouseY <= crd.bottom;


            requestAnimationFrame(function movePointer() {
                if (activePointer) {
                    pointerElem.classList.remove('box-pointer-hidden');
                    pointerElem.style.left = Math.floor(mouseX) + 'px';
                    pointerElem.style.top = Math.floor(mouseY) + 'px';
                } else {
                    pointerElem.classList.add('box-pointer-hidden');
                }
            });
        }

        function disablePointer() {
            requestAnimationFrame(function hidePointer() {
                pointerElem.classList.add('box-pointer-hidden');
            });
        }

        boxElem.addEventListener('mousemove', onMouseMove, false);
        boxElem.addEventListener('mouseleave', disablePointer, false);

    })();
}