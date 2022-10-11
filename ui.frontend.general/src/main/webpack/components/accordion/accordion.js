$(document).ready(function() {
	document.querySelectorAll(".accordion .title").forEach((title) => {
		title.addEventListener("click", (e) => { 
			let currentlyOpened = null;
			document.querySelectorAll(".accordion").forEach((accordian) => {
				if (accordian.classList.contains("open")) {
					currentlyOpened = accordian
					accordian.classList.remove("open");
				}
			});
			if (currentlyOpened != title.parentElement) {
				title.parentElement.classList.add("open");
			}
		});
	});
});