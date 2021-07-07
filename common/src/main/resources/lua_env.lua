--This is the main class that handles all things multis-related from the lua-side.
--Most of this behaviour is written in lua for both ease of use as well as efficiency (not having to move memory between
-- java and native code all the time)






--Global value shared between all objects in a pack.
--Only re-loads on a proper pack reload command.
packGlobals = {}

--Loads a script into the pack's lua environment.
-- src : The source code for the object, as a string.
-- table : The target table for the object. Will be cleared, or if nil, created.
function loadMultisObject(src, table)
    --Use or create table.
    local objectGlobal = getObjectSandboxTable(table)

    --Load the source code into a chunk.
    local objectScript = loadString(src)

    --Set global env for script.
    setfenv(objectScript, objectGlobal)

    debug.sethook(instructionLimit, 'c', 65536)
    --Run the script for the given item
    pcall(objectScript())

    return table
end


-- SANDBOXING --


--Returns a new table for an object that contains all the globals for it.
function getObjectSandboxTable(existingTable)
    --Create table
    local sandbox = existingTable or {}

    --Clear table.
    for k, v in pairs(sandbox) do sandbox[k] = nil end
    for i, v in ipairs(sandbox) do sandbox[i] = nil end

    --Set shared globals.
    sandbox.globals = packGlobals

    sandbox.math = mathProxy
    sandbox.table = tableProxy
    sandbox.string = stringProxy
    sandbox.assert = assert
    sandbox.pairs = pairs
    sandbox.ipairs = ipairs
    sandbox.next = next
    sandbox.select = select
    sandbox.pcall = pcall
    sandbox.xpcall = xpcall
    sandbox.print = print
    sandbox.tostring = tostring
    sandbox.tonumber = tonumber

    --Return table
    return sandbox
end

--Gets a read-only proxy to the original table.
function getReadonlyProxyTable(table)

    --keep local reference to table
    local tableReference = table
    --Create proxy
    local proxy = {}

    local mt = {
        --Index (getting a value from a table)
        --Just returns the value, it's safe to use this mostly.
        __index = function(t, k)
            return tableReference[k]
        end,

        --New index (putting a value into table).
        --Does nothing for readonly proxy tables.
        __newindex = function(t, k, v) end
    }

    setmetatable(proxy, mt)
end

local mathProxy = getReadonlyProxyTable(math)
local tableProxy = getReadonlyProxyTable(table)
local stringProxy = getReadonlyProxyTable(string)

--Function callback for instruction limit in debug hooks
function instructionLimit()
    error("Instruction limit hit!")
end


-- OBJECT MANAGEMENT --

--Registers a multis object into a table using a given script source.
--Clears and re-initializes the table for that object, if one exists. Otherwise, creates a new one.
function registerObject(target, src, id)
    target[id] = loadMultisObject(src, target[id])
end

--Unregisters a multis object from a table.
--Basically just sets the table for the object to nil.
function unregisterObject(target, id)
    target[id] = nil
end