CREATE TABLE "order" (
	id      uuid PRIMARY KEY,
	pet_id  uuid,
	"date"  timestamp with time zone,
	CONSTRAINT pet_id_fkey FOREIGN KEY (pet_id) REFERENCES pet(id)
)