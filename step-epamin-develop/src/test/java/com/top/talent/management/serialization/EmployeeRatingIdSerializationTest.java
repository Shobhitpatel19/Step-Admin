package com.top.talent.management.serialization;

import com.top.talent.management.entity.EmployeeRatingId;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeRatingIdSerializationTest {
    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        // Create test objects
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryId(1L);
        subCategory.setSubCategoryName("Test SubCategory");

        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setId(1L);
        version.setFileName("test.xlsx");
        version.setVersionName("v1");

        EmployeeRatingId id = new EmployeeRatingId(1L, subCategory, version);

        // Serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(id);
        }

        // Deserialize
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            EmployeeRatingId deserializedId = (EmployeeRatingId) ois.readObject();
            assertEquals(id.getUid(), deserializedId.getUid());
            assertEquals(id.getSubCategory().getSubCategoryId(), deserializedId.getSubCategory().getSubCategoryId());
            assertEquals(id.getTopTalentExcelVersion().getId(), deserializedId.getTopTalentExcelVersion().getId());
        }
    }
}
