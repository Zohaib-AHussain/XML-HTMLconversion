/* Copyright (c) 2012, Pragmatic Data, LLC. All rights reserved. */

function convertToTwoColumns() {
	convert(document.getElementById("Highlights"));
	convert(document.getElementById("Index"));
	addendaIndex();
}

var halfLength;
function convert(originalNode) {
	try {
		if (!originalNode)
			return;
		var firstDivElement = originalNode.children[0].children[0].children[0].children[0].children[0];
		var secondDivElement = originalNode.children[0].children[0].children[0].children[1].children[0];
		halfLength = secondDivElement.offsetHeight / 2;
		appendNode : while (true) {
			var firstChild = secondDivElement.children[0];
			var elementHeight = firstChild.offsetHeight;
			
			// This is to avoid the case of having just section text heading in the first column and its contents in second column
			if (firstChild.attributes && firstChild.attributes.getNamedItem("class") && firstChild.attributes.getNamedItem("class").nodeValue == "HighlightSection" && Math.abs(secondDivElement.offsetHeight - halfLength) < 20) {
				break appendNode;
			}
			
			if (secondDivElement.offsetHeight - elementHeight < halfLength) {
				moveToFirstTD(firstDivElement, secondDivElement);
				break appendNode;
				
			}
			
			firstDivElement.appendChild(firstChild);
		}
	} catch (e) {
		alert(e.message);
	}
}

function moveToFirstTD(firstDivElement, secondDivElement, innerElement, lastClonedNode) {
	
	var next = secondDivElement.children[0];
	var nextChild;
	if (innerElement) {
		if (!innerElement.children) {
			return;
		}
		next = innerElement.children[0];
	}
	while (next) {
		if (firstDivElement.done || /*next.getAttribute("class") == "HighlightSection" &&*/
			secondDivElement.offsetHeight < halfLength) {
			firstDivElement.done = true;
			return;
		}
		var child = next;
		next = next.nextElementSibling;
		var clonedNode;
		var childNodeName = child.nodeName.toLowerCase();
		var isListElement = childNodeName == "ul" || childNodeName == "ol" ? true : false;
		/* child.childElementCount does not work in IE 8 hence replaced with child.children.length */
		var copyCompleteElement = child.children.length == 0 || childNodeName == "h1" || childNodeName == "li" || childNodeName == "p" || childNodeName == "table" || childNodeName == "h2" || childNodeName == "dt" || childNodeName == "dd";
		if (copyCompleteElement) {
			clonedNode = child;  
			nextChild = child.nextSibling;
		 	if(lastClonedNode && (lastClonedNode.nodeName.toLowerCase() == "ul" || lastClonedNode.nodeName.toLowerCase() == "ol"))
				lastClonedNode = lastClonedNode.parentNode;
		} else if(isListElement){ /* Handling lists elements(ul,ol) separately - #1393 */
			var offsetHeightOfFirstDiv = firstDivElement.offsetHeight;
			if((offsetHeightOfFirstDiv + child.offsetHeight) < halfLength){
				copyCompleteElement = true;
				lastClonedNode.appendChild(child);
			} else {
				var list = document.createElement(childNodeName);
				var newoffsetHeightOfFirstDiv = offsetHeightOfFirstDiv;
				for(var i = 0; i < child.children.length; ){
					if(newoffsetHeightOfFirstDiv < halfLength || newoffsetHeightOfFirstDiv < secondDivElement.offsetHeight) {
						list.appendChild(child.children[i].cloneNode(true));
						newoffsetHeightOfFirstDiv = newoffsetHeightOfFirstDiv + child.children[i].offsetHeight;
						child.removeChild(child.children[i]);
					}
					else
						break;
				}
				lastClonedNode.appendChild(list);
				firstDivElement.done = true;
			}
			clonedNode = child;
			nextChild = child.nextSibling;
			lastClonedNode = child.parentNode;
		} else {
			clonedNode = child.cloneNode(false);
			if (child.attributes && child.attributes.getNamedItem("class") && child.attributes.getNamedItem("class").nodeValue == "HighlightSection") {
				child.attributes.removeNamedItem("class");
			}
		}
		
		if (lastClonedNode) {
			if (secondDivElement.offsetHeight > halfLength) { // TODO Decide whether to move the last element to left
				if(!isListElement)
					lastClonedNode.appendChild(clonedNode);
			} else {
				firstDivElement.done = true;
				return;
			}
		} else {
			firstDivElement.appendChild(clonedNode);
		}
		
		if (!firstDivElement.done) {
			if (copyCompleteElement) {
				moveToFirstTD(firstDivElement, secondDivElement, nextChild ? nextChild.parentNode : null, lastClonedNode ? lastClonedNode : clonedNode);
			} else {
				moveToFirstTD(firstDivElement, secondDivElement, child, lastClonedNode ? lastClonedNode : clonedNode);
			}
		}
		
	}
	
}

var _mixinStyleSheet = null;
function toggleMixin() {
  if(!_mixinStyleSheet) {
		if(document.styleSheets && document.getElementsByTagName("head").length > 0) {
      var styleSheetElement = document.createElement("style");
      styleSheetElement.type = "text/css";
			styleSheetElement.title = "SPL Hide Mixin Content";
      document.getElementsByTagName("head")[0].appendChild(styleSheetElement);
      _mixinStyleSheet = document.styleSheets[document.styleSheets.length - 1];
			if(_mixinStyleSheet.title != "SPL Hide Mixin Content") {
				_mixinStyleSheet = null;
				return;
			}
    }

    if(_mixinStyleSheet.insertRule)
      _mixinStyleSheet.insertRule(".spl .Mixin { display:none; }", 0);			
		else if(_mixinStyleSheet.addRule)
			_mixinStyleSheet.addRule(".spl .Mixin", "display:none;", -1);
		else
			return;
		_mixinState = -1;
	} else {
		_mixinStyleSheet.disabled = !_mixinStyleSheet.disabled;
	}
}

var href = document.location.href;
var docSetId
var isAccessData = href.indexOf("accessdata") > -1 ? true : false ;
function addendaIndex() {
		docSetId = document.getElementById("setId").textContent.replace(/Set id: /g,'');
		var url
		
		if(isAccessData) {
			 url = '../../../addenda-index.xml';
		} else {
			 url = '../../report/AddendaIndex.dat?referenceSetId=' + docSetId;
		}
		
		var request;
		
		if (window.XMLHttpRequest) {
			// IE7+, Firefox, Chrome, Opera, Safari
			request = new XMLHttpRequest();
		} else {
			// code for IE6, IE5
			request = new ActiveXObject('Microsoft.XMLHTTP');
		}
		// load
		request.open('GET', url, false);
		try{
			request.send();
		} catch(e) {
			return;
		}
		
		if (request.responseText != "" && request.status == 200) {
			if(isAccessData) {
				parser = new DOMParser();
				data = parser.parseFromString(request.responseText,"text/xml");
			} else {
				var data = datToJson(request.responseText);
			}
			insertData(data);
		}	
		
	}
	
function datToJson(datFileData) {
		var lines=datFileData.split("\n");
		var result = [];
		var headers=lines[0].split("|");
		for (var k=0;k<headers.length;k++) {
			headers[k]=headers[k].replace(/"/g,'');
		}
		for (var i=1;i< (lines.length - 1);i++) {
			var obj = {};
			var currentline=lines[i].split("|");
			for (var j=0;j<headers.length;j++) {
				obj[headers[j].replace(/"/g,'')] = currentline[j].replace(/"/g,'');
			}
			result.push(obj);
		}
	    return result; //JavaScript object
		//return JSON.stringify(result); //JSON
	}
		
	
function insertData(data) {
		var iDiv = document.createElement('div');
		var currentDiv = document.getElementsByClassName("DocumentTitle")[0];
		var para = document.createElement("h1");
		var node = document.createTextNode("Warning Letters Summary");
		currentDiv.appendChild(para);
		currentDiv.appendChild(iDiv);
		var ul = document.createElement('ul');
		var isSetIdPreset = false;
		
		if(isAccessData) { // xml data handling
			var temp = data.getElementsByTagName("addendum");
			for(var i=0; i < temp.length ; i++) {
				if(temp[i].getAttribute('referenceSetId') == docSetId) {
					var li = document.createElement('li');
					ul.appendChild(li);
					var a = document.createElement('a');
					var title = temp[i].childNodes[0];
					var linkText = document.createTextNode(title.data);
					a.appendChild(linkText);
					a.href = "../../spl/" + temp[i].getAttribute('documentId') + "/" + temp[i].getAttribute('documentId') + ".view";
					li.appendChild(a);
					isSetIdPreset = true;
				}
			}
		} else { //.dat data handling
			isSetIdPreset = true;
			var headers = Object.keys(data[0]);
			for(var i = 0; i < data.length ; i++) {
				var li = document.createElement('li');
				ul.appendChild(li);
				var a = document.createElement('a');
				var linkText = document.createTextNode(data[i][headers[10]]);
				a.appendChild(linkText);
				a.href = "../../spl/" + data[i][headers[5]] + "/" + data[i][headers[5]] + ".view";
				li.appendChild(a);
			}
		}
		if(isSetIdPreset) {
			iDiv.appendChild(ul); 
			para.appendChild(node);
		}
	}
	
