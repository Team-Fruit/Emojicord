var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    ASMAPI.log("INFO", "EmojicordTransformer initialized");
    var FontRendererTransform = this.engine.factory.scriptEngine.compile('Java.type(\"net.teamfruit.emojicord.asm.FontRendererTransform\");').eval();
    return {
        'FontRendererTransform': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.gui.FontRenderer'
            },
            'transformer': function (node) {
                ASMAPI.log("INFO", "EmojicordTransformer.FontRendererTransform start patching");
                new FontRendererTransform().apply(node)
                ASMAPI.log("INFO", "EmojicordTransformer.FontRendererTransform patched");
                return node;
            }
        }
    };
}