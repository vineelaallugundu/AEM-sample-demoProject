const dropdown = require('../components/dropdown-showhide-multivalue-select');

beforeAll(() => {
    global.jQuery = require('jquery');
});

it('should check showhide value is function', () => {
    jest.restoreAllMocks();
    global.window.HTMLMediaElement.prototype._mock = {
        value: function () {}
    }
    document.documentElement.innerHTML=
        `<coral class="coral-Form-field cq-dialog-dropdown-showhide-multival coral3-Select" data-cq-dialog-dropdown-showhide-target=".type-showhide-button-type" name="./type" labelledby="label_2ec86b8b-61b0-45f5-838c-08083c7164a6 description_2ec86b8b-61b0-45f5-838c-08083c7164a6" data-foundation-validation="" data-validation="" placeholder="" __vent-id__="1094" aria-invalid="false" aria-disabled="false">
<button is="coral-button" class="coral3-Button coral3-Button--secondary coral3-Button--block coral3-Select-button" size="M" variant="secondary" tracking="off" handle="button" type="button" block="" aria-haspopup="true" id="coral-id-649" aria-controls="coral-id-650" aria-expanded="false" aria-labelledby="label_2ec86b8b-61b0-45f5-838c-08083c7164a6 description_2ec86b8b-61b0-45f5-838c-08083c7164a6 coral-id-651"><coral-button-label></coral-button-label>
  <coral-icon class="coral3-Icon coral3-Select-openIcon coral3-Icon--chevronDown coral3-Icon--sizeXS" icon="chevronDown" size="XS" autoarialabel="on" handle="icon" role="img" aria-label="chevron down"></coral-icon>
  <span id="coral-id-651" handle="label" class=" coral3-Select-label">Link</span>
</button>
<coral-overlay class="coral3-Overlay coral3-Select-overlay" aria-hidden="true" tracking="off" handle="overlay" trapfocus="on" tabindex="0" focusonshow="on" role="presentation" offset="-1" alignmy="left top" alignat="left bottom" style="display: none;"><div coral-tabcapture="top" tabindex="0"></div><div coral-tabcapture="intermediate" tabindex="0"></div><div coral-tabcapture="bottom" tabindex="0"></div>
  <coral-selectlist class="coral3-SelectList coral3-Select-selectList" role="listbox" id="coral-id-650" tracking="off" handle="list" aria-multiselectable="false" style="max-height: 252px;"><coral-selectlist-item class="coral3-SelectList-item is-selected" role="option" value="link" selected="" tabindex="0" aria-disabled="false" aria-selected="true">Link</coral-selectlist-item><coral-selectlist-item class="coral3-SelectList-item" role="option" value="video" tabindex="-1" aria-selected="false" aria-disabled="false">Video</coral-selectlist-item><coral-selectlist-item class="coral3-SelectList-item" role="option" value="modal" tabindex="-1" aria-selected="false" aria-disabled="false">Modal</coral-selectlist-item><coral-selectlist-item class="coral3-SelectList-item" role="option" value="submit" tabindex="-1" aria-selected="false" aria-disabled="false">Submit</coral-selectlist-item></coral-selectlist>
</coral-overlay>
<input handle="input" type="hidden" name="./type" value="link">


<coral-select-item value="link" selected="" trackingelement="">Link</coral-select-item>
<coral-select-item value="video" trackingelement="">Video</coral-select-item>
<coral-select-item value="modal" trackingelement="">Modal</coral-select-item>
<coral-select-item value="submit" trackingelement="">Submit</coral-select-item>
<input class="foundation-field-related" type="hidden" name="./type@Delete"></coral>

<div class="type-showhide-button-type" data-showhidetargetvalue="link, video"><div class="coral-Form-fieldwrapper"><label id="label_29b3c8f5-cc12-400d-afc4-2ed697029cd5" class="coral-Form-fieldlabel">Link URL</label><span class="coral-Form-field coral-PathBrowser" data-init="pathbrowser" data-root-path="/content" data-option-loader="granite.ui.pathBrowser.pages.hierarchyNotFile" data-picker-src="/libs/wcm/core/content/common/pathbrowser/column.html/content?predicate=hierarchyNotFile" data-crumb-root="content" data-picker-multiselect="false" data-root-path-valid-selection="true">
    <span class="coral-InputGroup coral-InputGroup--block">
        <input class="coral-InputGroup-input js-coral-pathbrowser-input coral3-Textfield" type="text" name="./linkSrc" autocomplete="off" is="coral-textfield" aria-labelledby="label_29b3c8f5-cc12-400d-afc4-2ed697029cd5 description_29b3c8f5-cc12-400d-afc4-2ed697029cd5" value="https://www.youtube.com/shorts/0lRYsqIaquw" data-foundation-validation="" data-validation="" aria-invalid="false" aria-owns="coral-6" id="coral-7" aria-haspopup="true" aria-expanded="false">
        <span class="coral-InputGroup-button">
            <button class="js-coral-pathbrowser-button coral3-Button coral3-Button--secondary" type="button" title="Browse" is="coral-button" icon="folderSearch" iconsize="S" size="M" variant="secondary"><coral-icon class="coral3-Icon coral3-Icon--sizeS coral3-Icon--folderSearch" icon="folderSearch" size="S" autoarialabel="on" role="img" aria-label="folder search"></coral-icon><coral-button-label></coral-button-label></button>
        </span>
    </span>
<ul id="coral-6" class="coral-SelectList" role="listbox" aria-hidden="true" tabindex="-1"></ul></span><coral-icon class="coral-Form-fieldinfo coral3-Icon coral3-Icon--infoCircle coral3-Icon--sizeS" icon="infoCircle" tabindex="0" aria-describedby="description_29b3c8f5-cc12-400d-afc4-2ed697029cd5" alt="description" size="S" autoarialabel="on" role="img" aria-label="description"></coral-icon>
                <coral-tooltip target="_prev" placement="left" id="description_29b3c8f5-cc12-400d-afc4-2ed697029cd5" class="coral3-Tooltip coral3-Tooltip--info" aria-hidden="true" variant="info" tabindex="-1" role="tooltip" style="display: none;">
                    
                <coral-tooltip-content>Provide Link URL</coral-tooltip-content></coral-tooltip></div></div>

<div class="hide type-showhide-button-type" data-showhidetargetvalue="link, video"><div class="coral-Form-fieldwrapper"><label id="label_f8626cd0-66d3-40b7-a076-aa36bc27ce8b" class="coral-Form-fieldlabel">Link URL</label><span class="coral-Form-field coral-PathBrowser" data-init="pathbrowser" data-root-path="/content" data-option-loader="granite.ui.pathBrowser.pages.hierarchyNotFile" data-picker-src="/libs/wcm/core/content/common/pathbrowser/column.html/content?predicate=hierarchyNotFile" data-crumb-root="content" data-picker-multiselect="false" data-root-path-valid-selection="true">
    <span class="coral-InputGroup coral-InputGroup--block">
        <input class="coral-InputGroup-input js-coral-pathbrowser-input coral3-Textfield" type="text" name="./linkSrc" autocomplete="off" is="coral-textfield" aria-labelledby="label_f8626cd0-66d3-40b7-a076-aa36bc27ce8b description_f8626cd0-66d3-40b7-a076-aa36bc27ce8b" value="https://www.youtube.com/shorts/0lRYsqIaquw" data-foundation-validation="" data-validation="" aria-invalid="false" aria-owns="coral-10" id="coral-11" aria-haspopup="true" aria-expanded="false">
        <span class="coral-InputGroup-button">
            <button class="js-coral-pathbrowser-button coral3-Button coral3-Button--secondary" type="button" title="Browse" is="coral-button" icon="folderSearch" iconsize="S" size="M" variant="secondary"><coral-icon class="coral3-Icon coral3-Icon--sizeS coral3-Icon--folderSearch" icon="folderSearch" size="S" autoarialabel="on" role="img" aria-label="folder search"></coral-icon><coral-button-label></coral-button-label></button>
        </span>
    </span>
<ul id="coral-10" class="coral-SelectList" role="listbox" aria-hidden="true" tabindex="-1"></ul></span><coral-icon class="coral-Form-fieldinfo coral3-Icon coral3-Icon--infoCircle coral3-Icon--sizeS" icon="infoCircle" tabindex="0" aria-describedby="description_f8626cd0-66d3-40b7-a076-aa36bc27ce8b" alt="description" size="S" autoarialabel="on" role="img" aria-label="description"></coral-icon>
                <coral-tooltip target="_prev" placement="left" id="description_f8626cd0-66d3-40b7-a076-aa36bc27ce8b" class="coral3-Tooltip coral3-Tooltip--info" aria-hidden="true" variant="info" tabindex="-1" role="tooltip" style="display: none;">
                    
                <coral-tooltip-content>Provide Link URL</coral-tooltip-content></coral-tooltip></div></div>`;

    dropdown.showHide(global.window.HTMLMediaElement.prototype._mock, document.documentElement.innerHTML);
    expect($(document.documentElement.innerHTML).data('showhidetargetvalue')).toBe(undefined);
});


it('should check showHideHandler', () => {
    document.body.innerHTML=
        `<div className="buttons">
            <a href="javascript:void(0)" id="next">Next</a>
            <div id="results" role="alert"></div>
        </div>`;
    dropdown.showHideHandler(document.body);
    expect(document.body.className).toBe("");
});

it('should check handlerCondition', () => {

    document.documentElement.innerHTML=
        `<coral class="coral-Form-field cq-dialog-dropdown-showhide-multival coral3-Select" data-cq-dialog-dropdown-showhide-target=".type-showhide-button-type" name="./type" labelledby="label_2ec86b8b-61b0-45f5-838c-08083c7164a6 description_2ec86b8b-61b0-45f5-838c-08083c7164a6" data-foundation-validation="" data-validation="" placeholder="" __vent-id__="1094" aria-invalid="false" aria-disabled="false">
            </coral>
<div class="type-showhide-button-type" data-showhidetargetvalue="link, video">
<div class="coral-Form-fieldwrapper">
<label id="label_29b3c8f5-cc12-400d-afc4-2ed697029cd5" class="coral-Form-fieldlabel">Link URL</label>
<coral-tooltip target="_prev" placement="left" id="description_29b3c8f5-cc12-400d-afc4-2ed697029cd5" class="coral3-Tooltip coral3-Tooltip--info" aria-hidden="true" variant="info" tabindex="-1" role="tooltip" style="display: none;">
<coral-tooltip-content>Provide Link URL</coral-tooltip-content></coral-tooltip></div></div>`;
    dropdown.handlerCondition(document.documentElement.innerHTML);
});

it('should check condition', () => {
    dropdown.condition();
    expect($(".type-showhide-button-type").length).toBe(1);
});