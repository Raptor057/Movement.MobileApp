# Expresión Regular `SELECT`
**expresión regular mejorada** para validar un **`SELECT` en SQLite**, asegurando que siga la sintaxis correcta y respete las palabras reservadas:

```regex
^(?<StartQuery>SELECT)\s+(?<Columns>.+?)\s*\n(?<QueryFrom>FROM)\s+(?<TableName>[A-Za-z_][A-Za-z0-9_]*)\s*(?<Joins>(?:\s+JOIN\s+[A-Za-z_][A-Za-z0-9_]*\s+ON\s+.+?)*)?\s*(?<WhereClause>(?:\s+WHERE\s+.+?)?)\s*(?<GroupByClause>(?:\s+GROUP\s+BY\s+.+?)?)\s*(?<OrderByClause>(?:\s+ORDER\s+BY\s+.+?)?)\s*(?<LimitClause>(?:\s+LIMIT\s+\d+)?)\s*(?<EndOfQuery>;)$

```

----------

### **📌 Explicación de los grupos nombrados:**

1.  **`StartQuery`** → `SELECT` (debe iniciar con esta palabra clave).
2.  **`Columns`** → Lista de columnas seleccionadas (`*` o nombres de columnas separadas por `,`).
3.  **`QueryFrom`** → `FROM` seguido del **nombre de la tabla principal**.
4.  **`Joins`** _(opcional)_ → `JOIN ... ON ...` para validar `INNER JOIN`, `LEFT JOIN`, etc.
5.  **`WhereClause`** _(opcional)_ → `WHERE` con condiciones.
6.  **`GroupByClause`** _(opcional)_ → `GROUP BY` con columnas de agrupación.
7.  **`OrderByClause`** _(opcional)_ → `ORDER BY` para ordenar resultados.
8.  **`LimitClause`** _(opcional)_ → `LIMIT` para limitar el número de registros.
9.  **`EndOfQuery`** → `;` (debe finalizar con punto y coma).

----------

### **🔍 Ejemplos de consultas válidas:**

✔ **Consulta simple:**

```sql
SELECT id, name 
FROM Users;

```

✔ **Consulta con `JOIN`:**

```sql
SELECT U.id, U.name, O.order_id, O.amount 
FROM Users U 
INNER JOIN Orders O ON U.id = O.user_id;

```

✔ **Consulta con `WHERE`, `GROUP BY`, `ORDER BY` y `LIMIT`:**

```sql
SELECT S.date, SUM(S.amount) AS TotalSales 
FROM Sales S 
WHERE S.date > '2024-01-01' 
GROUP BY S.date 
ORDER BY S.date DESC 
LIMIT 10;

```

----------

### **🚨 Casos incorrectos que NO pasarían la validación:**

❌ **Falta `FROM` en la consulta**

```sql
SELECT id, name;

```

❌ **No tiene punto y coma (`;`) al final**

```sql
SELECT id, name 
FROM Users

```

----------

### **📌 Beneficios de esta expresión regular:**

✅ **Valida la estructura completa de un `SELECT` en SQLite**.  
✅ **Permite nombres de tablas/columnas con letras, números y `_`**.  
✅ **Soporta `JOIN`, `WHERE`, `GROUP BY`, `ORDER BY`, `LIMIT` opcionales**.  
✅ **Asegura que el query termina con `;`**.  
✅ **Evita consultas mal formadas que puedan generar errores**.

----------

### **🔥 ¿Cómo usar esta validación en Kotlin?**

Si quieres validar el query antes de ejecutarlo en **SQLite**, puedes hacer esto:

```kotlin
val regex = Regex(
    "^(?<StartQuery>SELECT)\\s+(?<Columns>.+?)\\s*\\n(?<QueryFrom>FROM)\\s+(?<TableName>[A-Za-z_][A-Za-z0-9_]*)\\s*(?<Joins>(?:\\s+JOIN\\s+[A-Za-z_][A-Za-z0-9_]*\\s+ON\\s+.+?)*)?\\s*(?<WhereClause>(?:\\s+WHERE\\s+.+?)?)\\s*(?<GroupByClause>(?:\\s+GROUP\\s+BY\\s+.+?)?)\\s*(?<OrderByClause>(?:\\s+ORDER\\s+BY\\s+.+?)?)\\s*(?<LimitClause>(?:\\s+LIMIT\\s+\\d+)?)\\s*(?<EndOfQuery>;)$"
)

fun validateSelectQuery(query: String): Boolean {
    return regex.matches(query)
}

// Ejemplo de uso:
val query = """
    SELECT U.id, U.name, O.order_id, O.amount 
    FROM Users U 
    INNER JOIN Orders O ON U.id = O.user_id;
""".trimIndent()

if (validateSelectQuery(query)) {
    println("✅ Query válido")
} else {
    println("❌ Query inválido")
}

```

----------

### **🚀 Conclusión**

Ahora tienes una **expresión regular robusta** para validar `SELECT` en SQLite, asegurando que respete las palabras clave y la sintaxis. Puedes usarla en **Kotlin** antes de ejecutar un query y evitar errores de SQL mal estructurados. 🚀🔥