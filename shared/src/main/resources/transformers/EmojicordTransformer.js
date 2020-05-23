var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    var transformers = {};
    ASMAPI.log("INFO", "EmojicordTransformer initialized");
    var EmojicordTransformer = this.engine.factory.scriptEngine.compile('Java.type(\"net.teamfruit.emojicord.asm.EmojicordTransformer\");').eval();
    var emojicordTransformer = new EmojicordTransformer();
    Array.prototype.forEach.call(emojicordTransformer.transformers,function(transform) {
        var simpleName = EmojicordTransformer.getSimpleClassName(transform);
        var className = transform.getClassName().getName();
        transformers[simpleName] = {
            'target': {
                'type': 'CLASS',
                'name': className
            },
            'transformer': function (node) {
                ASMAPI.log("INFO", "Patching" + className + " (class: " + node.name + ")");
                transform.apply(node)
                ASMAPI.log("INFO", "Finished Patching " + className + " (class: " + node.name + ")");
                return node;
            }
        };
    });
    return transformers;
}