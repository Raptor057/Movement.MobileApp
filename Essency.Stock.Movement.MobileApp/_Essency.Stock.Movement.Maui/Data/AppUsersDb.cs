using Essency.Stock.Movement.Maui.Models;
using Essency.Stock.Movement.Maui.Services;
using SQLite;

namespace Essency.Stock.Movement.Maui.Data
{
    public class AppUsersDb
    {
        private readonly SQLiteAsyncConnection _database;

        public AppUsersDb(SQLiteAsyncConnection database)
        {
            _database = database;
        }

        public async Task InitDatabase()
        {
            if (_database != null)
            {
                await _database.CreateTableAsync<AppUsers>();
            }
        }

        public async Task<List<AppUsers>> GetUsersList()
        {
            await InitDatabase();
            return await _database.Table<AppUsers>().ToListAsync();
        }

        public async Task<AppUsers> GetUserDataById(int id, string username)
        {
            await InitDatabase();
            return await _database.Table<AppUsers>()
                                   .FirstOrDefaultAsync(d => d.ID == id && d.UserName == username);
        }

        public async Task InsertUser(AppUsers usersData)
        {
            await InitDatabase();

            if (usersData == null || string.IsNullOrWhiteSpace(usersData.UserName))
                throw new ArgumentException("Invalid user data.");

            string normalizedUserName = usersData.UserName.ToLower().Trim();

            var existingUser = await _database.Table<AppUsers>()
                                              .FirstOrDefaultAsync(d => d.UserName.ToLower() == normalizedUserName);

            if (existingUser != null)
                throw new InvalidOperationException($"The username '{usersData.UserName}' already exists.");

            await _database.InsertAsync(usersData);
        }

        public async Task UpdateDataUser(AppUsers userData)
        {
            await InitDatabase();

            if (userData == null || userData.ID <= 0 || string.IsNullOrWhiteSpace(userData.UserName))
                throw new ArgumentException("Invalid user data.");

            var existingUser = await _database.Table<AppUsers>().FirstOrDefaultAsync(u => u.ID == userData.ID);

            if (existingUser == null)
                throw new InvalidOperationException($"User with ID {userData.ID} does not exist.");

            existingUser.Name = userData.Name;
            existingUser.LastName = userData.LastName;
            existingUser.IsAdmin = userData.IsAdmin;

            await _database.UpdateAsync(existingUser);
        }

        public async Task<bool> LoginUser(string userName, string password)
        {
            await InitDatabase();

            if (string.IsNullOrWhiteSpace(userName) || string.IsNullOrWhiteSpace(password))
                throw new ArgumentException("Invalid credentials.");

            string hashedPassword = PasswordHasher.HashPassword(password);

            var matchUser = await _database.Table<AppUsers>()
                                           .FirstOrDefaultAsync(d => d.UserName == userName && d.Password == hashedPassword);

            return matchUser != null;
        }
    }
}
