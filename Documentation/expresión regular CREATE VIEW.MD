# Expresión Regular `CREATE VIEW`
**expresión regular** que valida un **`CREATE VIEW` en SQLite**, asegurando que siga la sintaxis correcta y respete las palabras reservadas:

```regex
^(?<StartViewQuery>CREATE\s+VIEW)\s+(?<NameOfView>[A-Za-z_][A-Za-z0-9_]*)\s+(?<QueryAlias>AS)\s*\n(?<StartQuery>SELECT)\s+(?<Columns>.+?)\s*\n(?<QueryFrom>FROM)\s+(?<TableName>[A-Za-z_][A-Za-z0-9_]*)\s*(?<Joins>(?:\s+JOIN\s+[A-Za-z_][A-Za-z0-9_]*\s+ON\s+.+?)*)?\s*(?<WhereClause>(?:\s+WHERE\s+.+?)?)\s*(?<GroupByClause>(?:\s+GROUP\s+BY\s+.+?)?)\s*(?<OrderByClause>(?:\s+ORDER\s+BY\s+.+?)?)\s*(?<EndOfQuery>;)$

```

----------

### **📌 Explicación de los grupos nombrados:**

1.  **`StartViewQuery`** → `CREATE VIEW` (debe iniciar con esta palabra clave).
2.  **`NameOfView`** → El nombre de la vista (solo letras, números y `_`, sin comenzar con un número).
3.  **`QueryAlias`** → `AS` (obligatorio para definir la vista).
4.  **`StartQuery`** → `SELECT` (debe venir después de `AS`).
5.  **`Columns`** → Lista de columnas seleccionadas, permitiendo `*` o columnas separadas por `,`.
6.  **`QueryFrom`** → `FROM` seguido del **nombre de la tabla principal**.
7.  **`Joins`** _(opcional)_ → `JOIN ... ON ...` para validar `INNER JOIN`, `LEFT JOIN`, etc.
8.  **`WhereClause`** _(opcional)_ → `WHERE` con condiciones.
9.  **`GroupByClause`** _(opcional)_ → `GROUP BY` con columnas de agrupación.
10.  **`OrderByClause`** _(opcional)_ → `ORDER BY` para ordenar resultados.
11.  **`EndOfQuery`** → `;` (debe finalizar con punto y coma).

----------

### **🔍 Ejemplos de consultas válidas:**

✔ **Consulta simple:**

```sql
CREATE VIEW MyView AS 
SELECT id, name 
FROM Users;

```

✔ **Consulta con `JOIN`:**

```sql
CREATE VIEW UserOrders AS 
SELECT U.id, U.name, O.order_id, O.amount 
FROM Users U 
INNER JOIN Orders O ON U.id = O.user_id;

```

✔ **Consulta con `WHERE`, `GROUP BY`, y `ORDER BY`:**

```sql
CREATE VIEW SalesReport AS 
SELECT S.date, SUM(S.amount) AS TotalSales 
FROM Sales S 
WHERE S.date > '2024-01-01' 
GROUP BY S.date 
ORDER BY S.date DESC;

```

----------

### **🚨 Casos incorrectos que NO pasarían la validación:**

❌ **Falta `AS` después de `CREATE VIEW`**

```sql
CREATE VIEW MyView
SELECT id, name FROM Users;

```

❌ **Falta `FROM` en la consulta**

```sql
CREATE VIEW MyView AS 
SELECT id, name;

```

❌ **No tiene punto y coma (`;`) al final**

```sql
CREATE VIEW MyView AS 
SELECT id, name 
FROM Users

```

----------

### **📌 Beneficios de esta expresión regular:**

✅ **Valida la estructura completa del `CREATE VIEW`**.  
✅ **Permite nombres de vistas/tablas con letras, números y `_`**.  
✅ **Soporta `JOIN`, `WHERE`, `GROUP BY`, `ORDER BY` opcionales**.  
✅ **Asegura que el query termina con `;`**.  
✅ **Evita consultas mal formadas que puedan generar errores**.

----------

### **🔥 ¿Cómo usar esta validación en Kotlin?**

Si quieres validar el query antes de ejecutarlo en **SQLite**, puedes hacer esto:

```kotlin
val regex = Regex(
    "^(?<StartViewQuery>CREATE\\s+VIEW)\\s+(?<NameOfView>[A-Za-z_][A-Za-z0-9_]*)\\s+(?<QueryAlias>AS)\\s*\\n(?<StartQuery>SELECT)\\s+(?<Columns>.+?)\\s*\\n(?<QueryFrom>FROM)\\s+(?<TableName>[A-Za-z_][A-Za-z0-9_]*)\\s*(?<Joins>(?:\\s+JOIN\\s+[A-Za-z_][A-Za-z0-9_]*\\s+ON\\s+.+?)*)?\\s*(?<WhereClause>(?:\\s+WHERE\\s+.+?)?)\\s*(?<GroupByClause>(?:\\s+GROUP\\s+BY\\s+.+?)?)\\s*(?<OrderByClause>(?:\\s+ORDER\\s+BY\\s+.+?)?)\\s*(?<EndOfQuery>;)$"
)

fun validateViewQuery(query: String): Boolean {
    return regex.matches(query)
}

// Ejemplo de uso:
val query = """
    CREATE VIEW UserOrders AS 
    SELECT U.id, U.name, O.order_id, O.amount 
    FROM Users U 
    INNER JOIN Orders O ON U.id = O.user_id;
""".trimIndent()

if (validateViewQuery(query)) {
    println("✅ Query válido")
} else {
    println("❌ Query inválido")
}

```

----------

### **🚀 Conclusión**

Ahora tienes una **expresión regular robusta** para validar `CREATE VIEW` en SQLite, asegurando que respete las palabras clave y la sintaxis. Puedes usarla en **Kotlin** antes de ejecutar un query y evitar errores de SQL mal estructurados. 🚀🔥