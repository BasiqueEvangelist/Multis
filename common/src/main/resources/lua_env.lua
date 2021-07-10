--This is the main class that handles all things multis-related from the lua-side.
--Most of this behaviour is written in lua for both ease of use as well as efficiency (not having to move memory between
-- java and native code all the time)

print("Loading Multis Lua Environment")


--Global value shared between all objects in a pack.
--Only re-loads on a proper pack reload command.
objectSets = {}

--Loads a script into the pack's lua environment.
-- src : The source code for the object, as a string.
-- table : The target table for the object. Will be cleared, or if nil, created.
function loadMultisObject(src, id)

    --Split ID by namespace with :
    local splitID = {}
    for w in id:gmatch("([^:]+)") do
        table.insert(splitID, w)
    end

    local namespace = splitID[1] --Namespace for object.
    local location = splitID[2] --Object location.

    local set = getOrCreateSet(namespace) --The set to put this object into.

    local objectGlobal = getObjectSandboxTable(set, location) --The _G object for the object we're about to create.

    --Load the source code into a chunk.
    local objectScript = load(src, location, "bt" ,objectGlobal)


    debug.sethook(instructionLimit, '', 65536)
    --Run the script for the given item
    pcall(objectScript)

    return objectGlobal
end

function getOrCreateSet(namespace)
    local setTable = objectSets[namespace]

    --Creat set if needed
    if setTable == nil then
        setTable = {}
        objectSets[namespace] = setTable

        --Assign single variable: global
        --Name can never be taken because sets can only be assigned by minecraft identifiers, which can't be empty.
        setTable.globals = {}
    end

    return setTable
end

-- SANDBOXING --


--Returns a new table for an object that contains all the globals for it.
function getObjectSandboxTable(set, location)
    local objectTable = set[location] --Get table if it exist
    --Create one if it doesn't.
    if objectTable == nil then
        objectTable = {}
        set[location] = objectTable
    end

    --Create table
    local sandbox = objectTable

    --Clear table.
    for k, v in pairs(sandbox) do
        sandbox[k] = nil
    end
    for i, v in ipairs(sandbox) do
        sandbox[i] = nil
    end

    --Set shared globals.
    sandbox.globals = set.globals

    --Set sandbox whitelist
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
        __newindex = function(t, k, v)
        end
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


print("Environment Created!")