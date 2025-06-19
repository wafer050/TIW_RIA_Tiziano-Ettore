// TABLE SORTING MANAGEMENT FUNCTIONS

/*
 * Self invoking unnamed function. This generates a scope around the code which
 * causes variables and functions not to end up in the global scope.
 */

(function() {

	// Returns the text content of a cell.
	function getCellValue(tr, idx) {
		return tr.children[idx].textContent; // idx indexes the columns of the tr
		// row
	}

	function getOrder(v) {
		switch (v) {
			case "":
				return -Infinity;
			case "assente":
				return -49;
			case "rimandato":
				return -48;
			case "riprovato":
				return -47;
			case "30 e lode":
				return Infinity;
			default:
				return parseInt(v);
		}
	}

	/*
	   * Creates a function that compares two rows based on the cell in the idx
	   * position.
	   */
	function createComparer(idx, asc, isSpecialSort) {
		return function(a, b) {
			//var v1 = getCellValue(asc ? a : b, idx);
			//var v2 = getCellValue(asc ? b : a, idx);
			var v1 = getCellValue(a, idx);
			var v2 = getCellValue(b, idx);



			//voti da ordinare in maniera diversa rispetto al resto
			if (isSpecialSort) {
				return asc ? (getOrder(v1) - getOrder(v2)) : (getOrder(v2) - getOrder(v1));
			}
			else {
				// If non numeric value
				if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
					return asc ? v1.toString().localeCompare(v2) : v2.toString().localeCompare(v1);
					// lexical comparison
				}
				// If numeric value
				//return v1 - v2; 
				// v1 greater than v2 --> true
				return asc ? (v1 - v2) : (v2 - v1);
			}
		};
	}

	// For all table headers f class sortable
	document.querySelectorAll('th.sortable').forEach(function(th) {
		// Add a listener on the click event
		th.addEventListener('click', function() {


			var isSpecialSort = (th.id === "specialsort");
			//to avoid having to click twice to invert the order at the beginning
			if (isSpecialSort && typeof this.asc === 'undefined') {
				this.asc = true;
			}


			var table = th.closest('table'); // get the closest table tag
			// For every row in the table body
			// Use Array.from to build an array from table.querySelectorAll result
			// which is an Array Like Object (see DOM specifications)
			Array.from(table.querySelectorAll('tbody > tr'))
				// Toggle the criterion and to sort rows with the comparator function
				// passing
				// (index of column to compare, sort criterion asc or desc) --this is
				// the the
				// element




				//this.asc = undefined --> tue --> false
				.sort(createComparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc, isSpecialSort))
				// Append the sorted rows in the table body
				.forEach(function(tr) {
					table.querySelector('tbody').appendChild(tr);
				});
		});
	});
})(); // evaluate the function after its definition