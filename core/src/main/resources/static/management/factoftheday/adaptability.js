document.addEventListener("DOMContentLoaded", function () {
    function adjustTableDisplay() {
        const screenWidth = window.innerWidth;
        const tableWrapper = document.querySelector('.table-wrapper');
        const table = document.querySelector('.table');
        const rows = document.querySelectorAll('tbody tr');

        if (screenWidth < 768) {
            rows.forEach(row => {
                const accordion = document.createElement('div');
                accordion.classList.add('accordion-item');

                // Creating header for accordion
                const header = document.createElement('div');
                header.classList.add('accordion-header');
                header.textContent = row.querySelector('td:nth-child(2)').textContent; // Displaying ID as header
                accordion.appendChild(header);

                // Creating body for accordion
                const body = document.createElement('div');
                body.classList.add('accordion-body');

                const detailTable = row.querySelector('table');
                body.innerHTML = `<strong>Name:</strong> ${row.querySelector('td:nth-child(3)').textContent}<br>`;

                detailTable.querySelectorAll('td').forEach((td, index) => {
                    const label = ['TranslationID', 'Content', 'LanguageCode'][index % 3];
                    body.innerHTML += `<strong>${label}:</strong> ${td.textContent}<br>`;
                });

                body.innerHTML += `<strong>Creation date:</strong> ${row.querySelector('td:nth-child(7)').textContent}<br>`;

                const actions = row.querySelector('td:nth-child(8)').innerHTML;
                body.innerHTML += `<strong>Actions:</strong> ${actions}<br>`;

                accordion.appendChild(body);
                tableWrapper.appendChild(accordion);
            });

            table.style.display = 'none'; // Hide the table
        } else {
            table.style.display = 'table'; // Show the table
            tableWrapper.querySelectorAll('.accordion-item').forEach(accordion => accordion.remove());
        }
    }

    window.addEventListener('resize', adjustTableDisplay);
    adjustTableDisplay(); // Call on page load

    // document.addEventListener("click", function (e) {
    //     if (e.target.classList.contains('accordion-header')) {
    //         const item = e.target.parentElement;
    //         item.classList.toggle('active');
    //     }
    // });
});