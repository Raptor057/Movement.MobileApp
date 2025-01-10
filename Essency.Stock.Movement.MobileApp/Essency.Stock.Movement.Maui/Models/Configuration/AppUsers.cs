using SQLite;

namespace Essency.Stock.Movement.Maui.Models.Configuration
{
    public class AppUsers
    {
        [PrimaryKey, NotNull, AutoIncrement]
        public int ID { get; set; }

        [NotNull]
        public string UserName { get; set; }

        [NotNull]
        public string Name { get; set; }

        [NotNull]
        public string LastName { get; set; }

        [NotNull]
        public string Password { get; set; }

        [NotNull]
        public DateTime CreateUserDate { get; set; }

        [NotNull]
        public bool IsAdmin { get; set; }

        [NotNull]
        public bool Enable { get; set; }
    }
}
