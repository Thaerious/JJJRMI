"use strict;";
let package = {};
package.Simple = require("./Simple");
package.SelfRef = require("./SelfRef");
package.ReferenceEnum = require("./ReferenceEnum");
package.Primitives = require("./Primitives");
package.None = require("./None");
package.NonEmptySuper = require("./NonEmptySuper");
package.NonEmptyConstructor = require("./NonEmptyConstructor");
package.NoRetain = require("./NoRetain");
package.MyEnum = require("./MyEnum");
package.MissingConstructor = require("./MissingConstructor");
package.JJJObjectHasConstant = require("./JJJObjectHasConstant");
package.JJJAnnoHasConstant = require("./JJJAnnoHasConstant");
package.IsHandler = require("./IsHandler");
package.HasSuper = require("./HasSuper");
package.HasStatic = require("./HasStatic");
package.HasNone = require("./HasNone");
package.HasHandler = require("./HasHandler");
package.Has = require("./Has");
package.Foo = require("./Foo");
package.EmptyConstructor = require("./EmptyConstructor");
package.Constants = require("./Constants");

if (typeof module !== "undefined") module.exports = package;