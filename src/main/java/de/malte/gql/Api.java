package de.malte.gql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class Api {


    @Query("allData")
    @Description("Get all Films from a galaxy far far away")
    public List<ExampleData> getAllData() {
        List<ExampleData> result = new ArrayList<>();
        result.add(new ExampleData("Hamlet", "Shakespeare"));
        result.add(new ExampleData("Harry Potter", "Rowling"));
        return result;
    }


    public static class ExampleData {
        public String title;
        public String author;

        public ExampleData(String title, String author) {
            this.title = title;
            this.author = author;
        }
    }
}
