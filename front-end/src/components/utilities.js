// JS replica of Java's hash code method
String.prototype.hashCode = function(){
    var hash = 0;
    for (var i = 0; i < this.length; i++) {
        var character = this.charCodeAt(i);
        hash = ((hash<<5)-hash)+character;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
}

String.prototype.containsDisallowedChars = function(disallowedChars) {
    const regex = new RegExp(disallowedChars);
    if(this.match(regex)) {
        return true;
    }
    return false;
}