const _header = require('../components/_header');

beforeAll(() => {
    global.jQuery = require('jquery');
});

it('check resizeEnd', () => {
    document.body.innerHTML=
        `<div id="left-nav" class="left-nav d-none position-relative">
         		<div class="position-fixed">
         			<div class="logo pt-5 pb-5" onclick="goHome()">
         				<img class="mt-1" src="/" alt="logo" />
         			</div>
         			<nav>
                        <ul class="pt-4 display">
                            <li>
                                <a x-cq-linkchecker="skip" class="d-block pl-3 pt-2 pb-2" href="/">
                                    <span class="d-inline-block font-weight-bold">Label</span>
                                </a>
                            </li>
                        </ul>
         			</nav>
         		</div>
         	</div>`;
    _header.resizeEnd();
});

it('check methods', () => {
    document.body.innerHTML=
        `<div id="left-nav"></div> <div id="top-nav"></div>`;
    _header.toggleMenu(true);
    _header.toggleMenu(false);
    _header.initialize();
});