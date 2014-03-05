/* Add menu to extension bar */
ExtensionBar.addExtensionMenu({
	id : "extraction-extension",
	label : "E-mails, Urls and Phones extraction",
	submenu : [

	{
		id : "extraction-extension/about",
		label : "About...",
		click : dialogHandler(AboutDialog)
	} ]
});

/* Add submenu to column header menu */
DataTableColumnHeaderUI.extendMenu(function(column, columnHeaderUI, menu) {
	MenuSystem.appendTo(menu, "", [ { /* separator */}, {
		id : "extraction-extension/extraction",
		label : "Extract e-mails, URLs, etc.",
		click : dialogHandler(ObjectsDialog,column)
	} ]);
});

function dialogHandler(dialogConstructor) {
	var dialogArguments = Array.prototype.slice.call(arguments, 1);
	function Dialog() {
		return dialogConstructor.apply(this, dialogArguments);
	}
	Dialog.prototype = dialogConstructor.prototype;
	return function() {
		new Dialog().show();
	};
}




