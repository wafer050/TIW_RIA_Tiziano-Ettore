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

  /*
	 * Creates a function that compares two rows based on the cell in the idx
	 * position.
	 */
  function createComparer(idx, asc, isSpecialSort) {
    return function(a, b) {
      // get values to compare at column idx
      // if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
      var v1 = getCellValue(asc ? a : b, idx),
        v2 = getCellValue(asc ? b : a, idx);
      
		
		
		//voti da ordinare in maniera diversa rispetto al resto
		if(isSpecialSort){
			switch (v1){
				case "":
					return -Infinity;
				case "assente":
					if (v2 !== ""){
					return -Infinity;
					}
					else{
						return Infinity
					}
				case "rimandato":
					if (v2 !== "" && v2 !== "assente"){
					return -Infinity;
					}
					else{
						return Infinity;
					}
				case "riprovato":
					if (v2 !== "" && v2 !== "assente" && v2 !== "rimandato"){
						
					return -Infinity;
					}
					else{
						return Infinity;
					}
				case "30 e lode":
					return Infinity;
				default:
					return v1 - v2;
			}
		}
		else{
		    
		
		
		
		
		
		// If non numeric value
      if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
        return v1.toString().localeCompare(v2); // lexical comparison
      }
      // If numeric value
      return v1 - v2; // v1 greater than v2 --> true
	  		}
    };
  }

  // For all table headers f class sortable
  document.querySelectorAll('th.sortable').forEach(function(th) {
    // Add a listener on the click event
    th.addEventListener('click', function () {
		
		
		var isSpecialSort = (th.id === "specialsort");
		//to avoid having to click twice to invert the order at the beginning
		if (isSpecialSort && typeof this.asc ==='undefined'){
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