// Bind a method to an object and cache it
Object.defineProperty(Object.prototype, "extraBound", {
  value: function (methodName) {
    var boundName = "__extraBound__" + methodName;
    return this[boundName] || (this[boundName] = this[methodName].bind(this));
  },
});
