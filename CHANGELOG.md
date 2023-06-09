Updated `AiPlayer` to be more intelligent, shooting at coordinates surrounding successful hits
- I wanted to improve my AI to have a better chance in the BattleSalvo tournament
- I also updated my tests appropriately

Updated `Coord` record to contain `@JsonProperty` annotations
- This allows for easy serialization / deserialization by Jackson (although it may have worked without these)

Updated `Ship` with a new constructor to take length rather than just `ShipType`
Also added relevant `Ship` getter methods to allow for JSON serialization
- This again allows for easy serialization by Jackson and more flexibility when constructing `Ship`s

Remove the check in `AbstractPlayer` that a board must be initialized before the game can end
- This was not a very logical check to have in the first place, given PA04's context
- This also made testing a little bit easier, in one case