using SQLite;

namespace Essency.Stock.Movement.Maui.Models.AppConfigurations
{
    public class LogEntry
    {
        [PrimaryKey, AutoIncrement]
        public int ID { get; set; }

        public DateTime Timestamp { get; set; }

        public string LogLevel { get; set; } // Info, Warning, Error, etc.

        public string Message { get; set; }

        public string Exception { get; set; } // Opcional: para almacenar detalles de excepciones
    }
}
