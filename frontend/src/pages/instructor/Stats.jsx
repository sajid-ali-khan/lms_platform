import Header from "../../components/instructor/Header.jsx";

export default function Stats() {
  return (
    <div>
      <Header
        title="Course Stats"
        description="Performance metrics across your courses"
      />
      <div className="card-enterprise">
        <div className="card-enterprise-body empty-state-enterprise">
          <h3>Course Statistics</h3>
          <p>View enrollment trends, completion rates, and engagement metrics</p>
        </div>
      </div>
    </div>
  );
}
