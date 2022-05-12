# Translations

You can customize all messages that appear by creating a custom language file.

Create a new translations properties inside `translations/custom` with the filename being the language key you want to overwrite. For example `en.properties` or `de.properties`.

Copy the keys you need to overwrite from the default translations: [`platform/src/main/resources/schat_en.properties`][translations]

!!! important "Please Contribute Translations"
    Please contribute additional languages by creating a `schat_<language>.properties` inside `platform/src/main/resources/`.

```properties
--8<-- "platform/src/main/resources/schat_en.properties"
```

[translations]: https://github.com/sVoxelDev/sChat/blob/main/platform/src/main/resources/schat_en.properties
