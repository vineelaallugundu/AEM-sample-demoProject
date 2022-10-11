
Array.from(document.querySelectorAll("div.flip-card__front")).forEach((cardFront) => {
    cardFront.parentElement.style = `height:${cardFront.offsetHeight}px;`
});

Array.from(document.querySelectorAll("div.flip-card")).forEach((cardFlip) => {
    cardFlip.addEventListener("click", () => {
        cardFlip.classList.toggle("clicked");
    });
});